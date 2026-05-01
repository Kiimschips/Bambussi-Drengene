package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bambussi.typings.DefenceTyping;
import com.example.bambussi.typings.IntelligenceTyping;
import com.example.bambussi.typings.PowerTyping;
import com.example.bambussi.typings.SpeedTyping;
import com.example.bambussi.typings.TypeClass;

public class ChampSelect extends BaseMusicActivity {
    private Button btnPower, btnDefence, btnSpeed, btnIntelligent, btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_champ_select);

        // Start musik (fra BaseMusicActivity)
        startMusic(R.raw.pickakarater);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BattleManager.PlayerTeam.clear();

        // Find knapperne
        btnPower = findViewById(R.id.Power);
        btnDefence = findViewById(R.id.Defence);
        btnSpeed = findViewById(R.id.Speed);
        btnIntelligent = findViewById(R.id.Intelligent);
        btnStart = findViewById(R.id.startBattleBtn);

        // Aktiver Mute-knappen
        ImageButton btnMute = findViewById(R.id.btnMute);
        if (btnMute != null) {
            btnMute.setOnClickListener(v -> toggleMute());
        }

        // Sæt klik-lyttere til karaktervalg
        btnPower.setOnClickListener(v -> toggleFighter("Power"));
        btnDefence.setOnClickListener(v -> toggleFighter("Defence"));
        btnSpeed.setOnClickListener(v -> toggleFighter("Speed"));
        btnIntelligent.setOnClickListener(v -> toggleFighter("Intelligent"));

        // Start kamp knappen
        btnStart.setOnClickListener(v -> {
            if (BattleManager.PlayerTeam.size() == 3) {
                Intent intent = new Intent(this, Simple_Battle.class);
                Log.d("CHAMPSELECT", "Done");
                startActivity(intent);
                finish();
            }
        });
    }

    private void toggleFighter(String type) {
        // Tjek om karakteren allerede er valgt
        int existingIndex = -1;

        for (int i = 0; i < BattleManager.PlayerTeam.size(); i++) {
            if (BattleManager.PlayerTeam.get(i).getName().equals(type)) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex != -1) {
            // Fjern hvis den allerede er valgt (fortryd)
            BattleManager.PlayerTeam.remove(existingIndex);
        } else {
            // Tilføj hvis der er plads
            if (BattleManager.PlayerTeam.size() < 3) {
                switch (type) {
                    case "Power":
                        BattleManager.PlayerTeam.add(Fighter.createPower("Power"));
                        break;
                    case "Defence":
                        BattleManager.PlayerTeam.add(Fighter.createDefence("Defence"));
                        break;
                    case "Speed":
                        BattleManager.PlayerTeam.add(Fighter.createSpeed("Speed"));
                        break;
                    case "Intelligent":
                        BattleManager.PlayerTeam.add(Fighter.createIntelligence("Intelligent"));
                        break;
                }
            } else {
                Toast.makeText(this, "Du kan kun vælge 3 helte!", Toast.LENGTH_SHORT).show();
            }
        }
        updateUI();
    }

    private void updateUI() {
        // Nulstil alle tekster først
        btnPower.setText("Power");
        btnDefence.setText("Defence");
        btnSpeed.setText("Speed");
        btnIntelligent.setText("Intelligent");

        // Opdater teksten for dem der er valgt med deres rækkefølge
        for (int i = 0; i < BattleManager.PlayerTeam.size(); i++) {
            String name = BattleManager.PlayerTeam.get(i).getName();
            String statusText = name + " (Nr. " + (i + 1) + ")";
            
            if (name.equals("Power")) btnPower.setText(statusText);
            else if (name.equals("Defence")) btnDefence.setText(statusText);
            else if (name.equals("Speed")) btnSpeed.setText(statusText);
            else if (name.equals("Intelligent")) btnIntelligent.setText(statusText);
        }

        // Aktiver/deaktiver Start-knappen
        btnStart.setEnabled(BattleManager.PlayerTeam.size() == 3);
        if (BattleManager.PlayerTeam.size() == 3) {
            btnStart.setText("START Fight!");
        } else {
            btnStart.setText("Choose 3 Heroes(" + BattleManager.PlayerTeam.size() + "/3)");
        }
    }
}