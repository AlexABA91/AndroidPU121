package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidpu121.Orm.NbuRate;
import com.example.androidpu121.Orm.NbuRateResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class RatesActivity extends AppCompatActivity {
    private TextView tvJson;
    private final String nbuRatesUrl ="https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private NbuRateResponse nbuRateResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        tvJson = findViewById(R.id.rates_tv_json);
        new Thread(this::loadUrlData).start();
    }

    private void loadUrlData(){
        try (InputStream stream = new URL(nbuRatesUrl).openStream();){
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 16];
            int resivedLenght;
            while ((resivedLenght = stream.read(buffer))>0){
                builder.write(buffer,0,resivedLenght);
            }
            nbuRateResponse = new NbuRateResponse(
                    new JSONArray(builder.toString())
            );
            runOnUiThread(this::showResponse);
//            runOnUiThread(()->{
//                tvJson.setText(builder.toString());
//            });
        } catch( IOException | JSONException ex ) {
            Log.e("logUrlData", ex.getMessage()) ;
        }
        catch( NetworkOnMainThreadException ignored ) {
            Log.e("logUrlData",getString(R.string.rates_ex_thread)) ;
        }
    }
    private void showResponse(){
        LinearLayout container = findViewById(R.id.rates_container);
        for (NbuRate nbuRate : nbuRateResponse.getRates()){
             TextView tv = new TextView(this);
             tv.setText(String.format(
                     Locale.UK,
                     "%s(%s) %f грн",
                     nbuRate.getCc(),
                     nbuRate.getTxt(),
                     nbuRate.getRate()
             ));

             tv.setPadding(10,5,10,5);
             tv.setOnClickListener(this::rateClick);
             tv.setTag(nbuRate);
             container.addView(tv);
        }

    }
    private  void rateClick(View view){
        NbuRate nbuRate = (NbuRate) view.getTag();
        new AlertDialog.Builder(this, androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog)
                .setTitle(nbuRate.exchangedate)
                .setMessage(String.format(
                                Locale.UK,
                                "    %s(%s)\n\n     %f грн\n\n    %s",
                                nbuRate.getCc(),
                                nbuRate.getTxt(),
                                nbuRate.getRate(),
                                nbuRate.exchangedate))
                .setCancelable(false)
                .setNeutralButton("Ok",
                        (DialogInterface dialog, int witchButton)->dialog.cancel()
                ).show();
       // Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show();
    }



    private void showResponseTxt(){
        StringBuilder sb = new StringBuilder();
        for (NbuRate nbuRate : nbuRateResponse.getRates()){
            sb.append(String.format(
                    Locale.UK,
                    "%s(%s) %f грн\n",
                    nbuRate.getCc(),
                    nbuRate.getTxt(),
                    nbuRate.getRate()
            ));
        }
        tvJson.setText(sb.toString());
    }
}
/**
 *1. Основу работы с интернет осуществляет объект URL (похожий по содержанию с File).
 * 2. Сетевая активность запускается  попытка открыть URL openConnection() или
 * openStream()
 * при этом можно получить NetworkOnMainException, что свидетельствует
 * про подпитку открытия соединения
 * Этот Exception может не попасть в catch, смотрим в лагере.
 * 3. Подключение к интернату требует включенного WIFI на эмуляторе. Также розрешение
 * отсутствие которого приводит к исключению SecurityException
 * Данное разрешении должно быть указано в манифесте
 * <uses-permission android:name="android.permission.INTERNET"/>
 * иногда изменение преложеня требует переустановка на устройство
 *4. Вывод работы с интернатом в метод отдельного потока приводит к обратной проблемы связоной с
 * с исключением CalledfromWrongThreadExeption
 * которая заприщает изменять предстовления из другого потока
 * оброщатся нужно через делегирования  runOnUiThread(->)
 * 5. ORM после получения сырых данных их нужно отоброзить (map) на объекты и колекции
 * платформы (языка программирования). Для этого создают классы, которые отвечают структуре
 * данных и их конструкторы (фабрики ) которые принимают json
 */
