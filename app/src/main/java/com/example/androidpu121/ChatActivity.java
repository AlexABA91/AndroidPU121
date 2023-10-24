package com.example.androidpu121;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidpu121.Orm.ChatMassage;
import com.example.androidpu121.Orm.ChatResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private final String chatUrl = "https://chat.momentfor.fun/";
    private LinearLayout chatContainer;
    private final List<ChatMassage> chatMassages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatContainer = findViewById(R.id.chat_layout_container);
        new Thread(this::loadChatMessages).start();
    }
    private void showChatMessages(){
        boolean self= false;
        for(ChatMassage chatMassage : chatMassages){
            chatContainer.addView(chatMessageView(chatMassage, self));
            self = !self;
        }
    }
    private View chatMessageView(ChatMassage chatMassages,boolean self){
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

        if(self){
            messageContainer.setBackground(chat_message_self);
            containerParams.gravity = Gravity.END;
        }else{
            messageContainer.setBackground(chat_message);
            containerParams.gravity = Gravity.START;
        }
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