package com.example.zen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfilActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;

    private ImageView mImageView;
    private Button mCameraButton;
    private EditText mUserNameEditText;
    private EditText mPhoneEditText;
    private Button mSaveButton;

    private static final String userNameFileName = "user.txt";

    private static final String phoneFileName = "phone.txt";

    private static final String pfpFileName = "pfp.txt";

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mImageView = findViewById(R.id.image);
        mCameraButton = findViewById(R.id.camera_button);
        mUserNameEditText = findViewById(R.id.user_name_edittext);
        mPhoneEditText = findViewById(R.id.phone_edittext);
        mSaveButton = findViewById(R.id.save_button);

        mCameraButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        mSaveButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });


        String imagePath = readData(pfpFileName);
        if (imagePath != null) {
            File imgFile = new File(imagePath.trim());
            if (imgFile.exists()) {
                Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mImageView.setImageBitmap(imgBitmap);
            }
        }

        String userName = readData(userNameFileName).trim();
        if (userName != null) {
            mUserNameEditText.setText(userName);
        }

        String phone = readData(phoneFileName).trim();
        if (phone != null) {
            mPhoneEditText.setText(phone);
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                mImageView.setImageBitmap(bp);
                saveImage(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Action Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Zen/";

        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }

        File imageFile = new File(directory + fileName);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();


            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            imagePath = imageFile.getAbsolutePath();
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
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
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public void save() {
        if (imagePath != null)
            saveData(pfpFileName, imagePath);

        if (mUserNameEditText.getText().toString().trim() != null)
            saveData(userNameFileName, mUserNameEditText.getText().toString());
        if (mPhoneEditText.getText().toString().trim() != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                saveData(phoneFileName, mPhoneEditText.getText().toString());
                SmsManager smsManager = SmsManager.getDefault();
                String message = "Ce numéro a été enregistré sur Zen";
                try {
                    smsManager.sendTextMessage(mPhoneEditText.getText().toString().trim(), null, message, null, null);
                } catch(Exception e) {

                }

            }
        }
        Toast.makeText(this, "Sauvegarde confirmée", Toast.LENGTH_SHORT).show();
    }
}
