package com.example.zen;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zen.model.User;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView mAppNameText;
    private EditText mUserNameEditText;
    private Button mStartButton;
    private Button mProfilButton;
    private ImageView mlogoImage;
    private User user;

    private static final String userNameFileName = "user.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mlogoImage = findViewById(R.id.logo_image);
        mAppNameText = findViewById(R.id.app_name_text);
        mUserNameEditText = findViewById(R.id.user_name_edittext);
        mStartButton = findViewById(R.id.start_button);
        mProfilButton = findViewById(R.id.profil_button);

        mUserNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                mStartButton.setEnabled(!s.toString().isEmpty());
                if (s.toString().isEmpty())
                    mStartButton.setAlpha(.5f);
                else
                    mStartButton.setAlpha(1);
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getFirstName() == null || !user.getFirstName().equals(mUserNameEditText.getText().toString()))
                    saveData(userNameFileName, mUserNameEditText.getText().toString());
                user.setFirstName(mUserNameEditText.getText().toString());
                Intent sessionActivityIntent = new Intent(MainActivity.this, SessionActivity.class);
                startActivity(sessionActivityIntent);
            }
        });

        mProfilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getFirstName() == null || !user.getFirstName().equals(mUserNameEditText.getText().toString()))
                    saveData(userNameFileName, mUserNameEditText.getText().toString());
                user.setFirstName(mUserNameEditText.getText().toString());
                Intent sessionActivityIntent = new Intent(MainActivity.this, ProfilActivity.class);
                startActivity(sessionActivityIntent);
            }
        });

        user = new User();
        personalizeWelcomeMessage();
    }

    private void personalizeWelcomeMessage() {
        String firstName = readData(userNameFileName);
        if (firstName != null) {
            user.setFirstName(firstName);
            mUserNameEditText.setText(firstName);
            mUserNameEditText.setSelection(mUserNameEditText.length());
            mStartButton.setEnabled(true);
        } else {
            mStartButton.setEnabled(false);
            mStartButton.setAlpha(.5f);
        }
    }


    private void saveData(String fileName, String data) {
        try {
            FileOutputStream out = this.openFileOutput(fileName, MODE_PRIVATE);
            out.write(data.getBytes());
            out.close();
        } catch (Exception e) {
        }
    }

    private String readData(String fileName) {
        try {
            FileInputStream in = this.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            //Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }
}