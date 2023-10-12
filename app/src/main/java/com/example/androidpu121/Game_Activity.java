package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
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
    private final Random random= new Random();
    private int score;
    private int bestScore = 5000;
    private TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tvCells[i][j] =findViewById(
                        getResources().getIdentifier(
                                "game_cell_"+i+j,
                                "id",
                                getPackageName()
                        )
                );
            }
        }


       TableLayout tableLayout = findViewById(R.id.game_table);
        tableLayout.post( () -> {
                    int margin = 7;
            int w = this.getWindow().getDecorView().getWidth() - 2 * margin;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            layoutParams.setMargins(7, 50, 7, 50);
            tableLayout.setLayoutParams( layoutParams ) ;
        });
                tableLayout.setOnTouchListener(new OnSwipeListener(Game_Activity.this)
                {
                        @Override
                        public void onSwipeLeft() {
                            Toast.makeText( // сообщение что появляется и со временем исчезает
                                    Game_Activity.this,
                                    "onSwipeLeft",
                                    Toast.LENGTH_SHORT // время
                                    ).show();
                        }

                        @Override
                        public void onSwipeRight() {
                            Toast.makeText( // сообщение что появляется и со временем исчезает
                                    Game_Activity.this,
                                    "onSwipeRight",
                                    Toast.LENGTH_SHORT // время
                            ).show();
                        }

                        @Override
                        public void onSwipeTop() {
                            Toast.makeText( // сообщение что появляется и со временем исчезает
                                    Game_Activity.this,
                                    "onSwipeTop",
                                    Toast.LENGTH_SHORT // время
                            ).show();
                        }

                        @Override
                        public void onSwipeBottom() {
                            Toast.makeText( // сообщение что появляется и со временем исчезает
                                    Game_Activity.this,
                                    "onSwipeBottom",
                                    Toast.LENGTH_SHORT // время
                            ).show();
                        }
                    }
        );
        spanCell();
        spanCell();
        showField();
    }
    /**
     * Появление нового числа на поле
     * @return добавилось ли(есть свободные клетки)
     * */
    private boolean spanCell(){
        //Поскольку не известно скольео свободных ячеяк ищем все
        List<Integer> freeCellIndexes = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                     if(cells[i][j] == 0){
                         //складываем координаты в один индекс и сохранять
                         freeCellIndexes.add(10*i+j);
                     }
            }
        }
        // проверяем есть вообще свободные клетки
        int ctn = freeCellIndexes.size();

        if(ctn == 0){
            return false;
        }
        //генерируем случайный индекс
        int randIndex = random.nextInt(ctn);

        int randCellIndex = freeCellIndexes.get(randIndex);
        //розделяем на кординаты
        int x = randCellIndex / 10;
        int y = randCellIndex % 10;
        //генерируем случайное число : 2 (с вероятностью 09) или 4(вероятность 0,1)
        cells[x][y]= random.nextInt(10) == 0 ? 4 : 2;

        return true;
    };
    /**
     * Отображение текстовых данных на View и ключевой момент подбор стилей к значению числа */
    private void showField(){
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
                                "game_table_"+cells[i][j],
                                "style",
                                getPackageName()
                        )
                );
              // меняем background
              tvCells[i][j].setBackgroundResource(
                      resources.getIdentifier(
                              "game_tv_"+cells[i][j],
                              "drawable",
                              getPackageName()

                      )
              );
            }

        }
        score = random.nextInt(5000);

        if(score >= bestScore) bestScore = score;

        TextView tvBestScore = findViewById(R.id.game_tv_best);
        tvBestScore.setText(String.format(Locale.US,"BEST \n %d",bestScore));

        TextView tvScore =  findViewById(R.id.game_tv_score);
        tvScore.setText(String.format(Locale.US,"SCORE \n %d", score));

    }
}