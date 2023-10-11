package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class Game_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        findViewById(R.id.game_layout)
                .setOnTouchListener(new OnSwipeListener(Game_Activity.this)
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
    }
}