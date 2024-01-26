package com.example.kitchenactivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class KTActivity extends AppCompatActivity {

    LinearLayout timersLayout;
    EditText hoursInput, minutesInput, secondsInput, nameInput;
    Button startButton;

    ArrayList<MyCountDownTimer> timerList = new ArrayList<>();
    boolean isPaused = false;
    long remainingTime = 0; // Declare remainingTime here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ktactivity);

        timersLayout = findViewById(R.id.timersLayout);
        hoursInput = findViewById(R.id.hoursInput);
        minutesInput = findViewById(R.id.minutesInput);
        secondsInput = findViewById(R.id.secondsInput);
        startButton = findViewById(R.id.startButton);
         nameInput = findViewById(R.id.txtName);
        startButton.setOnClickListener(view -> startTimer());
    }

    void startTimer() {


        int hours = parseEditTextValue(hoursInput);
        int minutes = parseEditTextValue(minutesInput);
        int seconds = parseEditTextValue(secondsInput);

        long totalTime = (hours * 3600 + minutes * 60 + seconds) * 1000;
        String timerName =((TextView)findViewById(R.id.txtName)).getText().toString();

        MyCountDownTimer timeTimer = createNewTimer(totalTime,timerName);
        timerList.add(timeTimer);
    }

    int parseEditTextValue(EditText editText) {
        String valueStr = editText.getText().toString();
        if (!valueStr.isEmpty()) {
            return Integer.parseInt(valueStr);
        }
        return 0;
    }

    MyCountDownTimer createNewTimer(long time, String timerName) {
        final LinearLayout timerLayout = new LinearLayout(this);
        timersLayout.addView(timerLayout);

        final TextView timestamp = new TextView(this);
        timerLayout.addView(timestamp);

        Button cancelButton = new Button(this);
        cancelButton.setText("Cancel");
        timerLayout.addView(cancelButton);

        Button pauseButton = new Button(this);
        pauseButton.setText("Pause");
        timerLayout.addView(pauseButton);

        Button resetButton = new Button(this);
        resetButton.setText("Reset");
        timerLayout.addView(resetButton);

        MyCountDownTimer timeTimer = new MyCountDownTimer(this, time, 1000, timestamp, timerName) {
            public void onTick(long millisUntilFinished) {
                updateTimerUI(millisUntilFinished, timestamp, timerName);
            }

            public void onFinish() {
                updateTimerUI(0, timestamp, timerName);
            }
        };

        setupTimerButtons(timeTimer, cancelButton, pauseButton, resetButton);

        timeTimer.start();
        return timeTimer;
    }

    void updateTimerUI(long millisUntilFinished, TextView timestamp, String timerName) {
        remainingTime = millisUntilFinished; // Update remainingTime here
        timestamp.setText(timerName + "  Time remaining: " +
                millisUntilFinished / 1000 / 3600 + "h " +
                (millisUntilFinished / 1000 % 3600) / 60 + "m " +
                (millisUntilFinished / 1000 % 60) + "s");
    }

    void setupTimerButtons(final MyCountDownTimer timer, Button cancel, Button pause, Button reset) {
        cancel.setOnClickListener(view -> {
            timer.cancel();
            LinearLayout timerLayout = findTimerLayout(timer);
            if (timerLayout != null) {
                timersLayout.removeView(timerLayout);
                timerList.remove(timer);
            }
        });

        pause.setOnClickListener(view -> {
            if (isPaused) {
                // Resume the timer
                timer.resumeTimer();
                isPaused = false;
            } else {
                // Pause the timer
                timer.pauseTimer();
                isPaused = true;
            }
        });

        reset.setOnClickListener(view -> {
            timer.cancel();
            LinearLayout timerLayout = findTimerLayout(timer);
            if (timerLayout != null) {
                timersLayout.removeView(timerLayout);
                timerList.remove(timer);
                startTimer();
            }
        });
    }

    LinearLayout findTimerLayout(MyCountDownTimer timer) {
        for (int i = 0; i < timersLayout.getChildCount(); i++) {
            View child = timersLayout.getChildAt(i);
            if (child instanceof LinearLayout && timerList.get(i) == timer) {
                return (LinearLayout) child;
            }
        }
        return null;
    }

    class MyCountDownTimer extends CountDownTimer {
        private TextView timestamp;
        private String timerName;
        private boolean isTimerPaused = false;
        private long pausedTime = 0;

        MyCountDownTimer(Context context, long millisInFuture, long countDownInterval, TextView timestamp, String timerName) {
            super(millisInFuture, countDownInterval);
            this.timestamp = timestamp;
            this.timerName = timerName;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (!isTimerPaused) {
                remainingTime = millisUntilFinished;
                timestamp.setText(timerName + " - Time remaining: " +
                        millisUntilFinished / 1000 / 3600 + "h " +
                        (millisUntilFinished / 1000 % 3600) / 60 + "m " +
                        (millisUntilFinished / 1000 % 60) + "s");
            }
        }

        @Override
        public void onFinish() {
            updateTimerUI(0, timestamp, timerName);
        }

        void pauseTimer() {
            isTimerPaused = true;
            pausedTime = remainingTime;
            cancel();
        }

        void resumeTimer() {
            isTimerPaused = false;
            start();
        }

        void resetTimer() {
            isTimerPaused = false;
            cancel();
            remainingTime = 0;
            timestamp.setText("Timer reset");
        }
    }
}
