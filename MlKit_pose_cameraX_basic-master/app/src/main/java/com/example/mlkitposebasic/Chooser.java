package com.example.mlkitposebasic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mlkitposebasic.kotlin.LivePreviewActivity;

public class Chooser extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser);

        Button ARightButton = (Button) findViewById(R.id.ARightbutton);
        ARightButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View View) {
                Intent intent = new Intent(getApplicationContext(), AActionRight.class);
                startActivity(intent);
            }
        });

        Button ALeftButton = (Button) findViewById(R.id.ALeftbutton);
        ALeftButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View View) {
                Intent intent = new Intent(getApplicationContext(), AActionLeft.class);
                startActivity(intent);
            }
        });

        Button BButton = (Button) findViewById(R.id.Bbutton);
        BButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LivePreviewActivity.class);
                startActivity(intent);
            }
        });
    }
}

