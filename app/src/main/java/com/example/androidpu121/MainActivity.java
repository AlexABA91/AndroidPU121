package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // робота с элементами UI (Views) - только после setContentView
        tvTitle = findViewById(R.id.main_tv_title);
        Button btnViews =  findViewById(R.id.main_btn_views);
        btnViews.setOnClickListener(this::btnViewsClick);
        Button btnCalc =  findViewById(R.id.main_btn_calc);
        btnCalc.setOnClickListener(this::btnCalcClick);
        Button btnGame =  findViewById(R.id.main_btn_2048);
        btnGame.setOnClickListener(this::btnGameClick);
        Button btnRates =  findViewById(R.id.main_btn_rates);
        btnRates.setOnClickListener(this::btnRatesClick);
        Button btnChat =  findViewById(R.id.main_btn_chat);
        btnChat.setOnClickListener(this::btnChatClick);
    }

    //обработчики событий имеют одинаковый прототип
     private void btnViewsClick(View view){ // view -sender
         Intent intent = new Intent(this.getApplicationContext(),
                 ViewsActivity.class);
         startActivity(intent);
     }
    private void btnChatClick(View view){ // view -sender
        Intent intent = new Intent(this.getApplicationContext(),
                ChatActivity.class);
        startActivity(intent);
    }
    private void btnCalcClick(View view){ // view -sender
        Intent intent = new Intent(this.getApplicationContext(),
                CalcActivity.class);
        startActivity(intent);
    }
    private void btnGameClick(View view){ // view -sender
        Intent intent = new Intent(this.getApplicationContext(),
                Game_Activity.class);
        startActivity(intent);

    }
    private void btnRatesClick(View view){ // view -sender
        Intent intent = new Intent(this.getApplicationContext(),
                RatesActivity.class);
        startActivity(intent);
    }
}