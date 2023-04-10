package com.example.zen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.zen.model.NotificationHelper;

public class MeditationActivity extends AppCompatActivity {

    private TextView mTitleTextView;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private Button mPauseButton;
    private Button mResetButton;

    private int duree;
    private CountDownTimer timer;
    private long timeLeftInMillis;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        mTitleTextView = findViewById(R.id.title_textview);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressText = findViewById(R.id.progress_text);
        mPauseButton = findViewById(R.id.pause_button);
        mResetButton = findViewById(R.id.reset_button);

        Intent intent = getIntent();
        duree = intent.getIntExtra("duree", 0);
        timeLeftInMillis = 60000 * duree;
        mProgressBar.setMax(6000 * duree);
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
    }

    private void startTimer() {
        isRunning = true;
        MeditationActivity meditationActivity = this;
        timer = new CountDownTimer(timeLeftInMillis, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                mPauseButton.setText("START");
                mPauseButton.setEnabled(false);
                mPauseButton.setAlpha(0.5f);
                NotificationHelper notificationHelper = new NotificationHelper(meditationActivity);
                NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder("Zen Méditation", "Votre séance de méditation est terminée !");
                notificationHelper.notify(1, builder);
            }
        }.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
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

    @Override
    public void onBackPressed() {
        if (timer != null)
            timer.cancel();
        this.finish();
    }
}