package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidpu121.Orm.ChatMassage;
import com.example.androidpu121.Orm.ChatResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private final List<ChatMassage> chatMassages = new ArrayList<>();
    private ImageButton newMessage;
    private EditText etNik;
    private EditText etMessage;
    private MediaPlayer spawnMessageNew;
    private Animation newMessageAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatContainer = findViewById(R.id.chat_layout_container);
        new Thread(this::loadChatMessages).start();
        etNik = findViewById(R.id.chat_et_nik);
        etMessage = findViewById(R.id.chat_et_message);
        spawnMessageNew = MediaPlayer.create(ChatActivity.this,R.raw.jump_00);
        findViewById(R.id.chat_btn_send).setOnClickListener(this::sendButtonClick);
        newMessage = (ImageButton) findViewById(R.id.chat_btn_newMessage);
        newMessageAnimation = AnimationUtils.loadAnimation(

                ChatActivity.this,
                R.anim.chat_new_mesage_anim
        );
        newMessageAnimation.reset();
    }
    private void showChatMessages(){
        for(ChatMassage chatMassage : chatMassages){
            chatContainer.addView(chatMessageView(chatMassage));
        }

    }
    private void sendButtonClick(View view){
        String  nik = etNik.getText().toString();
        String  message = etMessage.getText().toString();
        if(nik.isEmpty()){
            Toast.makeText(this,"Введите ник в чат ",Toast.LENGTH_SHORT).show();
            etNik.requestFocus();
            return;
        }
        if(message.isEmpty()){
            Toast.makeText(this,"Введите сообщение",Toast.LENGTH_SHORT).show();
            etMessage.requestFocus();
            return;
        }
        new Thread(()->sendChatMessage(nik,message)).start();
    }
    private void sendChatMessage(String nik,String message){
        try {
            //Отправка POST в несколько шагов
            //1 Настраеваем соединение
            URL Url = new URL(chatUrl);
            HttpURLConnection connection = (HttpURLConnection) Url.openConnection();
            connection.setDoOutput(true); // запрос будет иметь тело
            connection.setDoInput(true); // ожидается ответ
            connection.setRequestMethod("POST");
            // заголовки устанавливаются как RequestProperty
            connection.setRequestProperty( "Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty( "Accept","*/*");
            connection.setChunkedStreamingMode(0);//не разделять на блоки (один поток)
            //2 Формируем запрос - пишем в Output
            OutputStream connectionOutput = connection.getOutputStream();
            String body = String.format("author=%s&msg=%s",nik,message);
            connectionOutput.write(body.getBytes(StandardCharsets.UTF_8));
            connectionOutput.flush();
            connectionOutput.close();

            // 3. Получаем Ответ читаем Input;
            int statusCode = connection.getResponseCode();
            if(statusCode == 201){// Ответ успех, тут тела не будет
                Log.d("sendChatMessage ","Request Ok");
            }else{// answer mistake, message about answer in body
                InputStream connectionInput = connection.getInputStream();
                String errorMessage = streamToString(connectionInput);
                Log.d("sendChatMessage ","Request failed with code "+ statusCode +" "+errorMessage);
            }

            connection.disconnect();
            if(statusCode == 201){//if request ok download message

               newMessage.setImageResource(R.drawable.chat_new_message);
               newMessage.startAnimation(newMessageAnimation);
               spawnMessageNew.start();

               loadChatMessages();
            }
        }catch (Exception ex){
            Log.d("sendChatMessage ",ex.getMessage());
        }
    }
    private View chatMessageView(ChatMassage chatMassages){
        LinearLayout messageContainer = new LinearLayout(ChatActivity.this);
        messageContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        Drawable chat_message = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_message
        );
        Drawable chat_message_self = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_message_self
        );

        //if(self){
            messageContainer.setBackground(chat_message_self);
            containerParams.gravity = Gravity.END;
//        }else{
//            messageContainer.setBackground(chat_message);
//            containerParams.gravity = Gravity.START;
//        }
        containerParams.setMargins(15, 3 ,15,3  );
        messageContainer.setPadding(15,5 ,15 ,5);
        messageContainer.setLayoutParams(containerParams);
        TextView tv = new TextView(ChatActivity.this);
        //name
        tv.setText(chatMassages.getAuthor());
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(16);
        messageContainer.addView(tv);
        // message
        tv = new TextView(ChatActivity.this);
        tv.setText(chatMassages.getText());
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setTextSize(18);
        tv.setPadding(0, 5 , 0, 5);
        messageContainer.addView(tv);
        //time
         tv = new TextView(ChatActivity.this);
         tv.setText(timeToView(chatMassages.getMoment()));
         tv.setTypeface(null, Typeface.ITALIC);
         tv.setTextSize(12);
        messageContainer.addView(tv);
      return messageContainer;
    }
    private  String timeToView(String time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss",Locale.getDefault());
        SimpleDateFormat BeforeData = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.UK);
        SimpleDateFormat onlyData = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        try {
            Date date = format.parse(time);
            Date now =new Date();
            if(date.before(now)){
                if( onlyData.parse(format.format(date))
                        .equals(  new Date (onlyData.parse(format.format(now))
                                .getTime() - (1000*60*60*24))
                        )){
                    return "вчера в "+new SimpleDateFormat("HH:mm", Locale.UK).format(date);
                }
                return BeforeData.format(date);
            }else {
                return "сегодня в "+ new SimpleDateFormat("HH:mm", Locale.UK).format(date);
            }
        } catch (ParseException e) {
            Log.e("timeToView","Parse exception: "+ e.getMessage());
        }
        return  time;
    }
    private void loadChatMessages(){
        try (InputStream inputStream = new URL(chatUrl).openStream()){
            ChatResponse chatResponse
                    = ChatResponse.fromJson(streamToString((inputStream)));
              // проверяем на новые сообщения. Обновляем коллекцию всех сообщений
            boolean wasNewMessage = false;
            chatResponse.getData().sort(Comparator.comparing(ChatMassage::getDate));
            for(ChatMassage mess: chatResponse.getData()){
                if(chatMassages.stream().noneMatch( m->m.getId().equals(mess.getId()) )){
                    //новое сообщение (нкт в коллекции)
                    chatMassages.add(mess);
                    wasNewMessage = true;
                }
            }
            if(wasNewMessage)
              runOnUiThread(  this::showChatMessages );
        } catch (android.os.NetworkOnMainThreadException ignored) {
            Log.e("loadChadMessage","NetworkOnMainThreadException");
        }catch (MalformedURLException ex){
            Log.e("loadChadMessage","URL parse error: "+ ex.getMessage());
        }catch (IOException ex){
            Log.e("loadChadMessage","IO error: "+ ex.getMessage());
        }
    }
    private String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream builder = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int byteResive;
        while ((byteResive = inputStream.read(buffer))>0){
            builder.write(buffer,0,byteResive);
        }
        return  builder.toString();
    }
}