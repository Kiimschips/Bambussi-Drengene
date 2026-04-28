package com.example.bambussi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends BaseMusicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Start musik via arv
        startMusic(R.raw.pickakarater);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            return insets;
        });

        // Mute knap
        findViewById(R.id.btnMute).setOnClickListener(v -> toggleMute());

        // NFC Knappen
        findViewById(R.id.btnBattle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToNFCReader();
            }
        });
    }

    public void ChangeToNFCReader(){
        startActivity(new Intent(this, NFCReader.class));
    }
}