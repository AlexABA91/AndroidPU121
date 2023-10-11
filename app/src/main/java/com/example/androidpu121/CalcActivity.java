package com.example.androidpu121;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;


public class CalcActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvExpression;
    private final int MAX_DIGITS = 10;
    private boolean nextDigit = false;
    private boolean nextExpresion = false;
    private double firstDigit;
    private double secondDigit;
    private String operator;
    private final DecimalFormat  format = new DecimalFormat("0.#");
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("result",tvResult.getText());
        outState.putCharSequence("expression",tvExpression.getText());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("result"));
        tvExpression.setText(savedInstanceState.getCharSequence("expression"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * При изменении конфигурации происходит пересозданние активности, поэтому данные теряются
        * дЛя сохранения данных задаются обработчики соответствующих событий
        * */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        tvResult = findViewById(R.id.calc_tv_result);
        tvResult.setText(R.string.calc_btn_0);
        tvExpression=findViewById(R.id.calc_tv_expression);
        tvExpression.setText("");
        findViewById(R.id.calc_btn_inverse).
                setOnClickListener(this::inverseClick);
        findViewById(R.id.calc_btn_plus).setOnClickListener(this::plusClick);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::expressionClearClick);
        for (int i = 0; i < 10; i++) {
            findViewById(
            getResources().getIdentifier(
                    "calc_btn_"+i,
                    "id",
                    getPackageName()
            )).setOnClickListener(this::digitClick);
        }
        findViewById(R.id.calc_btn_c).setOnClickListener(this::clearClick);
        findViewById(R.id.calc_btn_equal).setOnClickListener(this::equalClick);

    }

    private void equalClick(View view){
        if(parseExpression()){
            calculationOfTheResult();
        }
        if( tvResult.getText().length()!= 0){
           tvExpression.setText(String.format(Locale.US,"%s %s %s =",
                    format.format(firstDigit),
                    operator,
                    format.format(secondDigit)));
            nextDigit = true;
            nextExpresion = true;
        }
    }
   private void clearExpression(){
        tvExpression.setText("");
        nextExpresion =false;
   }
    private void plusClick(View view){
        if(nextExpresion){
            clearExpression();
        }

        if(nextDigit){return;}

       if(parseExpression()){
         calculationOfTheResult();
       }

        if( tvResult.getText().length()!= 0){
            String str = tvResult.getText().toString();
            str+=" + ";
            tvExpression.setText(str);
            nextDigit = true;
        }
    }

    private boolean parseExpression() {
      if(tvExpression.getText().toString().length() > 0) {
          String[] expressionArr = tvExpression.getText().toString().split(" ");
          firstDigit = getResult(expressionArr[0]);
          operator = expressionArr[1];
          secondDigit = getResult(tvResult.getText().toString());
          return  true;
      }
      return false;
    }

    private void expressionClearClick(View view){
        tvResult.setText(R.string.calc_btn_0);
    }
    private void clearClick(View view) {
        tvResult.setText(R.string.calc_btn_0);
        tvExpression.setText("");
    }
    private void inverseClick(View view) {
        if(nextExpresion ){
            clearExpression();
        }
        String str = tvResult.getText().toString();
        double arg = getResult(str);
        if(arg == 0){
            tvResult.setText(R.string.calc_div_zero_message);
        }else {
            str = "1/"+str + " = " ;
             tvExpression.setText(str);
            showResult(1/arg);
        }
    }
    private void digitClick(View view){

        String str = tvResult.getText().toString();
        if (str.equals((getString(R.string.calc_btn_0)))){
            str="";
        }
        if(nextDigit){
            nextDigit = false;
            str = "";
        }

        str += ((Button)view).getText();
        tvResult.setText(str);
    }
    private void showResult (double res){
        String str = String.valueOf(res);
        if(str.length()>MAX_DIGITS){
            str = str.substring(0,MAX_DIGITS);
        }
        str =  str.replaceAll("0", getString(R.string.calc_btn_0));
        tvResult.setText(str);
    }
    private double getResult(String str){
        str = str.replaceAll(getString(R.string.calc_btn_0),"0");
        str = str.replaceAll(getString(R.string.calc_btn_comma),".");

        return Double.parseDouble(str);
    }

    private void  calculationOfTheResult(){

        double res;
        switch (operator){
            case "+":
                res = firstDigit + secondDigit;
                break;
            default:
                res = 0;
        }

        tvResult.setText(format.format(res));
    }
}