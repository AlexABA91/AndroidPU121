package com.example.androidpu121;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class OnSwipeListener implements View.OnTouchListener {
    //2. Используем детектор жестов - не всю работу берем на себя
    private final GestureDetector gestureDetector;

    public OnSwipeListener(Context context) {
        // 3. Создаем конструктор который  требует контекст окружение активности (Activity)
        // приложения
        // 8. после реализация Swipe доб. в context
        this.gestureDetector = new GestureDetector(context,new SwipeGesureListener());
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //1. Это любое взаимодействие с экранном
        // задача - определить, что  это за действие, если она подпадает под признаки свайная то установить его направление
        //9. проедаем управление детектору жестов

        return gestureDetector.onTouchEvent(motionEvent);
    }
    //4. Создаем собственный класс который будет разбирать данные от жестов
    private final class SwipeGesureListener extends GestureDetector.SimpleOnGestureListener{
        //6. Высокое расширение экранов очень часто приводит к тому что даже одиночное касание экрана
        //считается  каr проведение fling на небольшое достояние приблизительно ~1мм  поэтому для улучшения взаимодействия следует
        // ввести границы расстояния (и скорости )  то такие действия не будет считаться свайпом
        private static final int MIN_DISTANCE = 100;
        private static final int MIN_VELOCITY = 100;
//        @Override
//        public boolean onDawn(@NonNull MotionEvent e){
//        }

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
                       return true;
        }

        @Override //5. Переопределяем метод fling
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false; // обработали ли мы действие
            float distanceX = e2.getX() - e1.getX(); // разница координат действий
            float distanceY = e2.getY() - e1.getY(); //  по кардиналам
            // abs - модуль числа без знака, определяем движение горизонтальный или вертикальный (идеальных не бывает)
            if(Math.abs(distanceX)> Math.abs(distanceY)){
                // по X (горизонталь) длина больше - считаем это горизонтальный свайп
                //проверяем только по оси X
                if(Math.abs(distanceX)> MIN_DISTANCE && Math.abs(velocityX)> MIN_VELOCITY){

                    result=true;

                    if (distanceX > 0 ){ // е1 ...->...e2
                        onSwipeRight();
                    }   else {// е2 ...->...e1
                        onSwipeLeft();
                    }
                }
            }else {
                //по X горизонтали длина меньше - вертикальный свайп
                //проверяем только по оси Y

                if(Math.abs(distanceY)> MIN_DISTANCE && Math.abs(velocityY)> MIN_VELOCITY){
                    result =true;           //e1.Y
                    if(distanceY > 0){      //e2.Y > e1.Y
                        onSwipeBottom();
                    }else {
                        onSwipeTop();
                    }
                }
            }
            return result;

        }
    }

    // ----------- наружный интерфейс
    // объявляем пустые методы для обозначения в обработчиках
    // Не делаем методы абстрактными для возможности перегрузки некоторых из них
    public void onSwipeLeft(){}
    public void onSwipeRight(){}
    public void onSwipeTop(){}
    public void onSwipeBottom(){}
}

/*
*           Детектор жестов
*  сенсорный экран имеет свой напор действий,
*  похожих на действия мыши но с отличиями. Плюс ряд действий на практике употреблены.
* Но не реализованы по умолчанию
* (свайпы - проведение пальцем по экрану по прямой лини )  */