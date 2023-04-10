package com.example.zen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.zen.model.NotificationHelper;

import java.util.Locale;

public class RespirationActivity extends AppCompatActivity {

    private ProgressBar mRespirationProgressBar;
    private TextView mRespirationProgressText;

    private TextView mTitleTextView;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private Button mPauseButton;
    private Button mResetButton;

    private int duree;
    private CountDownTimer timer;
    private long timeLeftInMillis;
    private boolean isRunning;

    private CountDownTimer timerRespiration;
    private long timeRespiration;
    private boolean inspirer;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respiration);

        mTitleTextView = findViewById(R.id.title_textview);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mPauseButton = findViewById(R.id.pause_button);
        mResetButton = findViewById(R.id.reset_button);
        mRespirationProgressBar = findViewById(R.id.respiration_progress_bar);
        mRespirationProgressText = findViewById(R.id.respiration_progress_text);

        Intent intent = getIntent();
        duree = intent.getIntExtra("duree", 0);
        timeLeftInMillis = 60000 * duree;
        mProgressBar.setMax(6000 * duree);
        timeRespiration = 5000;
        mRespirationProgressBar.setMax((int) timeRespiration);
        inspirer = true;
        isRunning = false;

        mPauseButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
                mPauseButton.setText("RESUME");
            } else {
                if (timeLeftInMillis == 60000 * duree)
                    startTimer();
                else
                    resumeTimer();
                mPauseButton.setText("PAUSE");
            }
        });
        mResetButton.setOnClickListener(v -> resetTimer());
        updateTimer();
        updateTimerRespiration();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.FRANCE);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    private void startTimer() {
        isRunning = true;
        RespirationActivity respirationActivity = this;
        timer = new CountDownTimer(timeLeftInMillis, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerRespiration.cancel();
                timeRespiration = 0;
                updateTimerRespiration();
                isRunning = false;
                mPauseButton.setText("START");
                mPauseButton.setEnabled(false);
                mPauseButton.setAlpha(0.5f);
                NotificationHelper notificationHelper = new NotificationHelper(respirationActivity);
                NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder("Zen Respiration", "Votre séance de respiration est terminée !");
                notificationHelper.notify(1, builder);

            }
        }.start();

        tts.speak("inspirer", TextToSpeech.QUEUE_FLUSH, null);
        inspirer = !inspirer;
        timerRespiration = new RespirationCountDownTimer(timeRespiration, 10).start();
    }

    public class RespirationCountDownTimer extends CountDownTimer {

        public RespirationCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeRespiration = millisUntilFinished;
            updateTimerRespiration();
        }

        @Override
        public void onFinish() {
            String text;
            if (inspirer)
                text = "inspirer";
            else
                text = "expirer";
            inspirer = !inspirer;
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            timeRespiration = 5000;
            timerRespiration = new RespirationCountDownTimer(timeRespiration, 10).start();
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timerRespiration.cancel();
            isRunning = false;
        }
    }

    private void resumeTimer() {
        if (timer != null) {
            startTimer();
            isRunning = true;
        }
    }

    private void resetTimer() {
        pauseTimer();

        timeLeftInMillis = 60000 * duree;
        updateTimer();
        timeRespiration = 5000;
        updateTimerRespiration();

        mPauseButton.setText("START");
        mPauseButton.setEnabled(true);
        mPauseButton.setAlpha(1);
    }

    private void updateTimer() {
        int progress = (int) (timeLeftInMillis / 1000);
        mProgressBar.setProgress((int) (timeLeftInMillis / 10));
        String timeLeft = String.format("%02d:%02d", progress / 60, progress % 60);
        mProgressText.setText(timeLeft);
    }

    private void updateTimerRespiration() {
        int progress = (int) (timeRespiration / 1000);
        mRespirationProgressBar.setProgress((int) timeRespiration);
        String timeLeft = String.valueOf(progress % 60);
        mRespirationProgressText.setText(timeLeft);
    }

    @Override
    public void onBackPressed() {
        if (timer != null)
            timer.cancel();
        if (timerRespiration != null)
            timerRespiration.cancel();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
