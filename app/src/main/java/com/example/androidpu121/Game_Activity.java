package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Game_Activity extends AppCompatActivity {
    /*
    Д.З. Завершити роботу з проєктом "2048"
    - Видавати повідомлення при натисканні кнопки "нова гра"
    - Забезпечити при повернені руху (undo) також повернення рахунку та рекорду (якщо треба)
    - Заблокувати роботу кнопки (undo) відразу після старту нової гри
    - Реалізувати перевірку позитивного завершення гри (набір 2048 хоча у на одній комірці)
       передбачити можливість продовження гри (повторні повідомлення не видавати)
    - Заблокувати повороти активності або реалізувати дизайн у ландшафтній орієнтації
    ** Повертання ходів далі ніж на один
     */
    private static final int N = 4;
    private final int[][] cells = new int[N][N];
    private final int[][] prevCells = new int[N][N];
    private final int[][] tmpCells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];
    private Switch SwitchSound;

    private Boolean IsSoundOn = true;
    private final Random random = new Random();
    private int score;
    private int bestScore;
    private TextView tvScore;
    private TextView tvBestScore;
    private Animation sawnCellAnimation;
    private Animation collapseGameAnimation;
    private MediaPlayer spawnSound;
    private static final String BEST_SCORE_FILE_NAME  = "best_score";

    private boolean move(MoveDirection direction){
        switch (direction){
            case BOTTOM: return moveBottom();
            case LEFT:   return moveLeft();
            case RIGHT:  return  moveRight();
            case TOP:    return moveTop();
        }
        return false;
    }
    private void startNewGame(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cells[i][j] = 0; }
        }

        score = 0;
        loadBestScore();
        tvBestScore.setText(getString(R.string.game_tv_best_score,bestScore));
        spanCell();
        spanCell();
        showField();
    }
    private void processMove(MoveDirection direction){
        for (int i = 0; i <N ; i++) {
            System.arraycopy(cells[i], 0, tmpCells[i], 0, N);
        }
        if (move(direction)) {
            for (int i = 0; i <N ; i++) {
                System.arraycopy(tmpCells[i], 0, prevCells[i], 0, N);
            }
            spanCell();
            showField();
            if(isGameFail()){ // нет больше ходов
                showFailDialog();
            }else {
                if(score > bestScore){
                    bestScore = score;
                    saveBestScore();
                    tvBestScore.setText(getString(R.string.game_tv_best_score,bestScore));
                }
            }
        } else {
            Toast.makeText( // сообщение что появляется и со временем исчезает
                    Game_Activity.this,
                    R.string.game_toast_no_move,
                    Toast.LENGTH_SHORT // время
            ).show();
        }
    }
    private void showFailDialog(){
        new AlertDialog.Builder(this, androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.game_over)
                .setMessage(R.string.game_over_dialog)
                .setCancelable(false)
                .setNeutralButton(R.string.game_over_yes, (DialogInterface dialog, int whichButton)->
                        startNewGame()
                )
                .setPositiveButton(R.string.game_over_undo, (DialogInterface dialog, int whichButton)->
                        finish() // закрывает активность
                )
                .setNegativeButton(R.string.game_over_no, (DialogInterface dialog, int whichButton)->
                        finish() // закрывает активность
                )
                .show();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        spawnSound = MediaPlayer.create(Game_Activity.this,R.raw.jump_00);
        tvScore = findViewById(R.id.game_tv_score);
        //animation
        tvBestScore = findViewById(R.id.game_tv_best);
        sawnCellAnimation = AnimationUtils.loadAnimation(
                Game_Activity.this,
                R.anim.game_spavn_cell
        );

        sawnCellAnimation.reset();

        collapseGameAnimation = AnimationUtils.loadAnimation(
            Game_Activity.this,
            R.anim.game_collapse_cells
        );
        collapseGameAnimation.reset();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j] = findViewById(
                        getResources().getIdentifier(
                                "game_cell_" + i + j,
                                "id",
                                getPackageName()
                        )
                );
            }
        }

        SwitchSound = findViewById(R.id.switch1);
        SwitchSound.setOnClickListener(this::SoundOnOf);
        SwitchSound.setChecked(true);

        TableLayout tableLayout = findViewById(R.id.game_table);
        tableLayout.post(() -> {
            int margin = 7;
            int w = this.getWindow().getDecorView().getWidth() - 2 * margin;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            layoutParams.setMargins(7, 50, 7, 50);
            tableLayout.setLayoutParams(layoutParams);
        });
        tableLayout.setOnTouchListener(new OnSwipeListener(Game_Activity.this) {
                                           @Override
                                           public void onSwipeLeft() {
                                               processMove(MoveDirection.LEFT);
                                           }

                                           @Override
                                           public void onSwipeRight() {
                                               processMove(MoveDirection.RIGHT);
                                           }

                                           @Override
                                           public void onSwipeTop() {
                                               processMove(MoveDirection.TOP);
                                           }

                                           @Override
                                           public void onSwipeBottom() {
                                               processMove(MoveDirection.BOTTOM);
                                           }
                                       }
        );
        findViewById(R.id.game_btn_new).setOnClickListener(this::newGameClick);
        findViewById(R.id.game_btn_undo).setOnClickListener(this::undoMoveClick);

        loadBestScore();
        startNewGame();

    }
    private void newGameClick(View view)
    {
        startNewGame();
    }
    private void undoMoveClick(View view){
        for (int i = 0; i <N ; i++) {
            System.arraycopy(prevCells[i], 0, cells[i], 0, N);
        }
        showField();
    }

    private void saveBestScore(){
        /*
        В Android имеется розприделенная файловая система. В приложении есть доступ к приватным файлам
        которые есть частью автоматической работы которые удаляются вместе с приложением.
        Есть общее ресурсы (картинки, загрузки, и т.д.)
        доступ к которым указывается в манифесте и должно разрешатся пользователем.
        Другие файлы могут быть недоступны
         */
        try(FileOutputStream outputStream = openFileOutput(BEST_SCORE_FILE_NAME, Context.MODE_PRIVATE);
            DataOutputStream writer = new DataOutputStream(outputStream)){
            writer.writeInt(bestScore);
            writer.flush();
            Log.d("savaBestScore","save OK");

        } catch (IOException e) {
            Log.e("savaBestScore", Objects.requireNonNull(e.getMessage()));
        }
    }
    private void loadBestScore(){
        try(FileInputStream inputStream = openFileInput(BEST_SCORE_FILE_NAME);
            DataInputStream reader = new DataInputStream(inputStream);
        ) {
            bestScore = reader.readInt();
            Log.d("LoadBestScore","Beast score read: "+ bestScore);
        }  catch (IOException e) {
            bestScore = 0;
            Log.e("LoadBestScore", Objects.requireNonNull(e.getMessage()));
        }

    }
    private void SoundOnOf(View view) {
        IsSoundOn = !IsSoundOn;
    }
    /**
     * Появление нового числа на поле
     *
     * @return добавилось ли(есть свободные клетки)
     */
    private boolean spanCell() {
        //Поскольку не известно скольео свободных ячеяк ищем все
        List<Integer> freeCellIndexes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    //складываем координаты в один индекс и сохранять
                    freeCellIndexes.add(10 * i + j);
                }
            }
        }
        // проверяем есть вообще свободные клетки
        int ctn = freeCellIndexes.size();

        if (ctn == 0) {
            return false;
        }
        //генерируем случайный индекс
        int randIndex = random.nextInt(ctn);

        int randCellIndex = freeCellIndexes.get(randIndex);
        //розделяем на кординаты
        int x = randCellIndex / 10;
        int y = randCellIndex % 10;
        //генерируем случайное число : 2 (с вероятностью 09) или 4(вероятность 0,1)
        cells[x][y] = random.nextInt(10) == 0 ? 4 : 2;
       // назначаем анимацию появления для данной клеткиs
        if(IsSoundOn){
            spawnSound.start();
        }
       tvCells[x][y].startAnimation(sawnCellAnimation);
        return true;
    }
    /**
     * Отображение текстовых данных на View и ключевой момент подбор стилей к значению числа
     */
    private void showField() {
        // особенность некоторые параметры на лету можно менять через стили
        // для некоторых нужно давать отдельные инструкции

        Resources resources = getResources();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                //устанавливаем текст
                tvCells[i][j].setText(String.valueOf(cells[i][j]));

                //Меняем стиль в равенстве числа celse[i][j]
                tvCells[i][j].setTextAppearance(
                        resources.getIdentifier(
                                "game_table_" + cells[i][j],
                                "style",
                                getPackageName()
                        )
                );
                // меняем background
                tvCells[i][j].setBackgroundResource(
                        resources.getIdentifier(
                                "game_tv_" + cells[i][j],
                                "drawable",
                                getPackageName()

                        )
                );
            }
            ;
        }
        TextView tvBestScore = findViewById(R.id.game_tv_best);
        tvBestScore.setText(getString(R.string.game_tv_best_score, bestScore));

        TextView tvScore = findViewById(R.id.game_tv_score);
        tvScore.setText(getString(R.string.game_tv_score, score));

    }
    private boolean moveLeft() {
        boolean result = false;
        // все ячейки с числами перемещаем на лево одна к другой
//        // проверяем и копируем проверяем после коллапса

        // 1) пересування: оскільки невідомо відразу на скільки позицій буде пересування
        // [0020] [2020] [0202] будемо повторювати переміщення доки ситуація не зміниться
        boolean needRepeat;
        for (int i = 0; i < N; i++) { // цикл по рядах
            // проробляємо і-й ряд
            do {
                needRepeat = false;
                for (int j = 0; j < N - 1; j++) {
                    if (cells[i][j] == 0 && cells[i][j + 1] != 0) {
                        cells[i][j] = cells[i][j + 1];
                        cells[i][j + 1] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            //2) collapse
            for (int j = 0; j < N - 1; j++) {
                if (cells[i][j] != 0 &&
                        cells[i][j] == cells[i][j + 1]) {
                    cells[i][j] *= 2;

                    tvCells[i][j].startAnimation(collapseGameAnimation);

                    score += cells[i][j];
                    //передвинуть на место колапсированой клетки все которые правей
                    //
                    for (int k = j + 1; k < N-1; k++) {
                        cells[i][k] = cells[i][k + 1];
                    }
                    cells[i][N-1] = 0;
                    result = true;
                }
            }
        }
        return result;
    }
    private boolean moveRight(){
        boolean result = false;
        boolean needRepeat;
        for (int i = 0; i < N; i++) {
            do {
                needRepeat = false;
                for (int j = N-1; j >0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            }while (needRepeat);
            for (int j = N-1; j >0; j--) {
                if (cells[i][j] != 0 &&cells[i][j] == cells[i][j-1]) {
                    cells[i][j] *= 2;

                    tvCells[i][j].startAnimation(collapseGameAnimation);

                    score += cells[i][j];
                    //передвинуть на место колапсированой клетки все которые правей
                    //
                    for (int k = j-1; k > 0; k--) {
                        cells[i][k] = cells[i][k -1];
                    }
                    cells[i][0] = 0;
                    result = true;
                }
            }
        }
        return result;
    }
    private boolean moveTop(){
        boolean result = false;
        boolean needRepeat;
        for (int i = 0; i < N; i++) { // цикл по рядах
            // проробляємо і-й ряд
            do {
                needRepeat = false;
                for (int j = 0; j <N-1; j++) {
                    if (cells[j][i] == 0 && cells[j+1][i] != 0) {
                        cells[j][i] = cells[j+1][i] ;
                        cells[j+1][i] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            //2) collapse
            for (int j = 0; j<N-1; j++) {
                if (cells[j][i] == cells[j+1][i] &&
                    cells[j+1][i] != 0) {
                    cells[j][i] *= 2;

                    tvCells[j][i].startAnimation(collapseGameAnimation);

                    score += cells[j][i];
                    //передвинуть на место колапсированой клетки все которые выше
                    //
                    for (int k = j+1;k < N-1;k++) {
                        cells[k][i] = cells[k+1][i];
                    }
                    cells[N-1][i] = 0;
                    result = true;
                }
            }
        }


        return result;
    }
    private boolean moveBottom(){
        boolean result = false;

        boolean needRepeat;
        for (int i = 0; i < N; i++) { // цикл по рядах
            // проробляємо і-й ряд
            do {
                needRepeat = false;
                for (int j = N-1; j > 0; j--) {
                    if (cells[j][i] == 0 && cells[j-1][i] != 0) {
                        cells[j][i] = cells[j-1][i];
                        cells[j-1][i] = 0;
                        needRepeat = true;
                        result = true;
                    }
                }
            } while (needRepeat);
            //2) collapse
            for (int j = N-1; j >0; j--) {
                if (cells[j][i] != 0 &&
                        cells[j][i] == cells[j-1][i]) {

                    cells[j][i] *= 2;

                    tvCells[j][i].startAnimation(collapseGameAnimation);

                    score += cells[j][i];
                    //передвинуть на место колапсированой клетки все которые выше
                    //
                    for (int k = j-1;k > 0;k--) {
                        cells[k][i] = cells[k-1][i];
                    }
                    cells[0][i] = 0;
                    result = true;
                }
            }
        }
        return  result;
    }
    private boolean isGameFail(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] == 0) {
                    return false;
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N-1; j++) {
                if (cells[i][j] == cells[i][j + 1]) {
                    return false;
                }
            }
        }
        for (int i = 0; i < N-1; i++) {
            for (int j = 0; j < N; j++)  {
                if (cells[i][j] == cells[i + 1][j]) {
                    return false;
                }
            }
        }
        return  true;
    }
    private enum MoveDirection{
        BOTTOM,
        LEFT,
        RIGHT,
        TOP

    }
}
//Анимации (double-anim)--плавные переходы числовых параметров между начальным и канечным значением
//Закладываются деклоративно и прорабатываются OC
// Создаем папку (amin)