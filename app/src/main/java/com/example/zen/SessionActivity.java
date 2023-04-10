package com.example.zen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SessionActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private Button mMeditationButton;
    private Button mRespirationButton;

    private TextView numberPickerTextView;
    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        mTitleTextView = findViewById(R.id.title_textview);
        mMeditationButton = findViewById(R.id.meditation_button);
        mRespirationButton = findViewById(R.id.respiration_button);
        numberPickerTextView = findViewById(R.id.number_picker_textview);
        numberPicker = findViewById(R.id.number_picker);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(5);


        mMeditationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, MeditationActivity.class);
                intent.putExtra("duree", numberPicker.getValue());
                startActivity(intent);
            }
        });

        mRespirationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, RespirationActivity.class);
                intent.putExtra("duree", numberPicker.getValue());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
