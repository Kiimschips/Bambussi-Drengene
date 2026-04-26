package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class ChampSelect extends AppCompatActivity {
    private Button btnPower, btnDefence, btnSpeed, btnIntelligent, btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_champ_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find knapperne
        btnPower = findViewById(R.id.Power);
        btnDefence = findViewById(R.id.Defence);
        btnSpeed = findViewById(R.id.Speed);
        btnIntelligent = findViewById(R.id.Intelligent);
        btnStart = findViewById(R.id.startBattleBtn);

        // Sæt klik-lyttere til karaktervalg
        btnPower.setOnClickListener(v -> toggleFighter("Power", 100, 30, new PowerTyping()));
        btnDefence.setOnClickListener(v -> toggleFighter("Defence", 150, 15, new DefenceTyping()));
        btnSpeed.setOnClickListener(v -> toggleFighter("Speed", 80, 25, new SpeedTyping()));
        btnIntelligent.setOnClickListener(v -> toggleFighter("Intelligent", 90, 40, new IntelligenceTyping()));

        // Start kamp knappen
        btnStart.setOnClickListener(v -> {
            if (BattleManager.PlayerTeam.size() == 3) {
                Intent intent = new Intent(this, Simple_Battle.class);
                //intent.putExtra("MY_TEAM", BattleManager.PlayerTeam);
                Log.d("CHAMPSELECT", "Done");
                startActivity(intent);
                finish();
            }
        });
    }

    private void toggleFighter(String name, int hp, int dmg, TypeClass type) {
        // Tjek om karakteren allerede er valgt
        int existingIndex = -1;

        for (int i = 0; i < BattleManager.PlayerTeam.size(); i++) {
            if (BattleManager.PlayerTeam.get(i).getName().equals(name)) {
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
                BattleManager.PlayerTeam.add(new Fighter(name, hp, dmg, type));
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
            btnStart.setText("START KAMPEN!");
        } else {
            btnStart.setText("Vælg 3 helte (" + BattleManager.PlayerTeam.size() + "/3)");
        }
    }
}