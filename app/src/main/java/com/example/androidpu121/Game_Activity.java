package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Game_Activity extends AppCompatActivity {
    private static final int N = 4;
    private final int[][] cells = new int[4][4];
    private final TextView[][] tvCells = new TextView[N][N];
    private Switch SwitchSound;
    private Boolean IsSoundOn = true;
    private final Random random = new Random();
    private int score;
    private int bestScore;
    private TextView tvScore;
    private Animation sawnCellAnimation;
    private Animation collapseGameAnimation;
    private MediaPlayer spawnSound;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        spawnSound = MediaPlayer.create(Game_Activity.this,R.raw.jump_00);
        tvScore = findViewById(R.id.game_tv_score);
        //animation
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
                                               if (moveLeft()) {
                                                   spanCell();
                                                   showField();
                                               } else {
                                                   Toast.makeText( // сообщение что появляется и со временем исчезает
                                                           Game_Activity.this,
                                                           R.string.game_toast_no_move,
                                                           Toast.LENGTH_SHORT // время
                                                   ).show();
                                               }
                                           }

                                           @Override
                                           public void onSwipeRight() {
                                               if(moveRight()){
                                                   spanCell();
                                                   showField();
                                               }else {
                                               Toast.makeText( // сообщение что появляется и со временем исчезает
                                                       Game_Activity.this,
                                                       R.string.game_toast_no_move,
                                                       Toast.LENGTH_SHORT // время
                                               ).show();}
                                           }

                                           @Override
                                           public void onSwipeTop() {
                                               if(moveTop()){
                                                   spanCell();
                                                   showField();
                                               }
                                               else{
                                               Toast.makeText( // сообщение что появляется и со временем исчезает
                                                       Game_Activity.this,
                                                       "onSwipeTop",
                                                       Toast.LENGTH_SHORT // время
                                               ).show();}
                                           }

                                           @Override
                                           public void onSwipeBottom() {
                                               if(moveBottom()){
                                                   spanCell();
                                                   showField();
                                               }
                                               else{
                                               Toast.makeText( // сообщение что появляется и со временем исчезает
                                                       Game_Activity.this,
                                                       R.string.game_toast_no_move,
                                                       Toast.LENGTH_SHORT // время
                                               ).show();}
                                           }
                                       }
        );
        spanCell();
        spanCell();
        showField();
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

    ;

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

        }

        if (score >= bestScore) bestScore = score;

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
}
//Анимации (double-anim)--плавные переходы числовых параметров между начальным и канечным значением
//Закладываются деклоративно и прорабатываются OC
// Создаем папку (amin)