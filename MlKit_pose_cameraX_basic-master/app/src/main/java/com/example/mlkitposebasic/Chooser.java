package com.example.mlkitposebasic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mlkitposebasic.kotlin.BActionLeft;
import com.example.mlkitposebasic.kotlin.BActionRight;

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

        Button BRightButton = (Button) findViewById(R.id.BRightbutton);
        BRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BActionRight.class);
                startActivity(intent);
            }
        });

        Button BLeftButton = (Button) findViewById(R.id.BLeftbutton);
        BLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BActionLeft.class);
                startActivity(intent);
            }
        });
    }
}

