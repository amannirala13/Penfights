package com.asdev.penfights;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.asdev.penfights.helper.CustomToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(MainActivity.this, Genre.class));


    }

    private void showToast(String MESSAGE) {

        CustomToast toast = new CustomToast(this);
        toast.setMessage(MESSAGE);
        toast.setLongDuration();
        toast.show();
    }
}
