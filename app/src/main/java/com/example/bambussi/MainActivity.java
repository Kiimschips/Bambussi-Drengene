package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
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

        // Mute knap (ImageButton)
        ImageButton btnMute = findViewById(R.id.btnMute);
        if (btnMute != null) {
            // Sæt ikon baseret på om der er lyd eller ej
            btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            
            btnMute.setOnClickListener(v -> {
                toggleMute();
                // Opdater ikonet efter tryk
                btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            });
        }

        // NFC Knappen
        findViewById(R.id.btnBattle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeToNFCReader();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMusic(R.raw.pickakarater);
        // Sørg for at ikonet er korrekt når vi vender tilbage til skærmen
        ImageButton btnMute = findViewById(R.id.btnMute);
        if (btnMute != null) {
            btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
        }
    }

    public void ChangeToNFCReader(){
        startActivity(new Intent(this, NFCReader.class));
    }

    public void goToChampSelect(View v) {
        startActivity(new Intent(this, ChampSelect.class));
    }
}