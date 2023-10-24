package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.icu.text.Collator;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
// TODO: сделать в модальном окне конвертор валют
public class RatesActivity extends AppCompatActivity {
    private TextView tvJson;
    private final String nbuRatesUrl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private NbuRateResponse nbuRateResponse;
    private List<NbuRate> shownRates = new ArrayList<>();
    EditText input;
    Button resetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        new Thread(this::loadUrlData).start();
        findViewById(R.id.rates_btn_by_alpha)
                .setOnClickListener(this::byAlphaClick);
        findViewById(R.id.rates_btn_by_curse)
                .setOnClickListener(this::byCoursClick);

       resetBtn = findViewById(R.id.rates_btn_reset);
       resetBtn.setOnClickListener(this::resetRatesArray);

       input = findViewById(R.id.rates_pt_search);
       input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchByCc(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void resetRatesArray(View view) {
        input.setText("");
        showResponse();
    }

    private void searchByCc(String inputstring){
        shownRates = nbuRateResponse.getRates().stream()
                .filter(c->c.getCc().contains(inputstring))
                .collect(Collectors.toList());
        showResponse();
    }
    private void byCoursClick(View view) {
        if(nbuRateResponse == null|| nbuRateResponse.getRates() == null){
            Toast.makeText(this, "No data",Toast.LENGTH_SHORT).show();
            return;
        }
        nbuRateResponse.getRates().sort(Comparator.comparing(NbuRate::getRate)); // Comparator.comparing(NbuRate::getTxt));
        showResponse();
    }

    private void byAlphaClick(View view){
        if(nbuRateResponse == null|| nbuRateResponse.getRates() == null){
            Toast.makeText(this, "No data",Toast.LENGTH_SHORT).show();
            return;
        }
        nbuRateResponse.getRates().sort((r1,r2)->
                Collator.getInstance(Locale.forLanguageTag("UK")
                ).compare(r1.getTxt(),r2.getTxt())); // Comparator.comparing(NbuRate::getTxt));
        showResponse();
    }
    private void loadUrlData() {
        try (InputStream stream = new URL(nbuRatesUrl).openStream();) {
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 16];
            int resivedLenght;
            while ((resivedLenght = stream.read(buffer)) > 0) {
                builder.write(buffer, 0, resivedLenght);
            }
            nbuRateResponse = new NbuRateResponse(
                    new JSONArray(builder.toString())
            );
            runOnUiThread(this::showResponse);
//
        } catch (IOException | JSONException ex) {
            Log.e("logUrlData", ex.getMessage());
        } catch (NetworkOnMainThreadException ignored) {
            Log.e("logUrlData", getString(R.string.rates_ex_thread));
        }
    }
    private void showResponse() {
        if(input.getText().equals(""))
            shownRates = nbuRateResponse.getRates();

        LinearLayout container = findViewById(R.id.rates_container);
        container.removeAllViews();
        Drawable rateBg1 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_1
        );
        Drawable rateBg2 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_2
        );
        Drawable rateBg3 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_3
        );
        Drawable rateBg4 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_4
        );
        Drawable rateBg5 = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.rate_shape_5
        );
        LinearLayout.LayoutParams rateParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout.LayoutParams centerMargin= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout.LayoutParams lineParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        rateParams.setMargins(5,10,5,10);
        rateParams.gravity = Gravity.CENTER;
        lineParams.setMargins(5,5,5,5);

        centerMargin.setMargins(5,0,5,0);
        boolean isFirs = true;
        for (NbuRate nbuRate : shownRates) {
            LinearLayout line = new LinearLayout(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TextView tv3 = new TextView(this);
            line.setLayoutParams(lineParams);
            if (isFirs) {
                line.setBackground(rateBg1);
            } else {
                line.setBackground(rateBg5);
            }
            isFirs = isFirs ? false:true;
            line.setPadding(5, 5,5,5 );
            tv1.setText(nbuRate.getCc()+" ");
            tv1.setMinWidth(50);
            tv1.setLayoutParams(rateParams);
            tv1.setBackground(rateBg2);
            tv1.setPadding(5,5,5,5);
            tv1.setTextSize(20);
            tv2.setText("1 - " + nbuRate.getTxt()+" ");
            tv2.setTextSize(16);
            tv2.setBackground(rateBg3);
            tv2.setPadding(5,5,5,5);
            tv2.setLayoutParams(centerMargin);
            tv2.setLayoutParams(rateParams);
            tv3.setText(String.format(" = %,.3f грн",nbuRate.getRate()));
            tv3.setBackground(rateBg4);
            tv3.setPadding(10,5,10,5);
            tv3.setLayoutParams(rateParams);
            tv3.setTextSize(18);
            tv3.setMinWidth(100);
            line.addView(tv1);
            line.addView(tv2);
            line.addView(tv3);
            line.setTag(nbuRate);
            line.setOnClickListener(this::rateClick);
            container.addView(line);

//            TextView tv = new TextView(this);
//            tv.setText(String.format(
//                    Locale.UK,
//                    "%s (%s) %f грн",
//                    nbuRate.getCc(),
//                    nbuRate.getTxt(),
//                    nbuRate.getRate()
//            ));
//            tv.setBackground(rateBg1);
//            tv.setPadding(10, 5, 10, 5);
//            tv.setLayoutParams(rateParams);
//            tv.setOnClickListener(this::rateClick);
//            tv.setTag(nbuRate);
//            container.addView(tv);
        }

    }
    private void rateClick(View view) {
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
                        (DialogInterface dialog, int witchButton) -> dialog.cancel()
                ).show();
        // Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show();
    }
    private void showResponseTxt() {
        StringBuilder sb = new StringBuilder();
        for (NbuRate nbuRate : nbuRateResponse.getRates()) {
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
 * 1. Основу работы с интернет осуществляет объект URL (похожий по содержанию с File).
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
 * 4. Вывод работы с интернатом в метод отдельного потока приводит к обратной проблемы связоной с
 * с исключением CalledfromWrongThreadExeption
 * которая заприщает изменять предстовления из другого потока
 * оброщатся нужно через делегирования  runOnUiThread(->)
 * 5. ORM после получения сырых данных их нужно отоброзить (map) на объекты и колекции
 * платформы (языка программирования). Для этого создают классы, которые отвечают структуре
 * данных и их конструкторы (фабрики ) которые принимают json
 */
