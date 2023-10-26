package com.example.androidpu121.Orm;
import android.icu.text.SimpleDateFormat;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.sql.Date;
import java.text.ParseException;
import java.util.Locale;

public class ChatMassage {
  private String id;
  private String author;
  private String text;
  private String moment;
  private final static SimpleDateFormat sqlDateFormat =
    new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss", Locale.UK);

  public java.util.Date getDate()
  {
      try {
        return sqlDateFormat.parse(getMoment());
      } catch (ParseException ignored) {
        return null;
  }
  }
  public static ChatMassage from (JSONObject jsonObject){
    ChatMassage chatMassage = new ChatMassage();
    try{
    for (Field field : ChatMassage.class.getDeclaredFields() ) {
        if (jsonObject.has(field.getName())) {
            field.set(chatMassage,
                    jsonObject.getString((field.getName())));
        }
    }}catch (Exception ignored){}
    return chatMassage;
  }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }


}
