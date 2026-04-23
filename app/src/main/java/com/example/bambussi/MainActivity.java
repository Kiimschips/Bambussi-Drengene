package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnBattle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToNFCReader();
            }
        });
    }
    public void ChangeToNFCReader(){
        startActivity(new Intent(this, Simple_Battle.class));
    }

    public void goToChampSelect(View v) {
        startActivity(new Intent(this, ChampSelect.class));
    }
}
