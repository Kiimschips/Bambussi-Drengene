package com.example.bambussi;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int instances = 0;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button changeButton = (Button) findViewById(R.id.button);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToNFCReader();
            }
        });

        findViewById(R.id.button3).setOnClickListener(v ->{
            Toast.makeText(MainActivity.this, "ID: " + id, Toast.LENGTH_SHORT).show();
        });

        instances++;
        id = instances;
    }

    public void ChangeToNFCReader() {
        Intent intent = new Intent(this, NFCReader.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(new Intent(intent));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Main - Paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Main"+id+" - Destroyed", Toast.LENGTH_SHORT).show();
    }

}

