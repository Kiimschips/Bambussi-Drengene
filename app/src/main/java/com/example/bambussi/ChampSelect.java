package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
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

        // Start musik
        startMusic(R.raw.pickakarater);

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
            btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            btnMute.setOnClickListener(v -> {
                toggleMute();
                btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            });
        }

        // Sæt klik-lyttere til karaktervalg med deres billeder
        btnPower.setOnClickListener(v -> toggleFighter("Power", 100, 30, new PowerTyping(), R.drawable.powerfront, R.drawable.powerback));
        btnDefence.setOnClickListener(v -> toggleFighter("Defence", 150, 15, new DefenceTyping(), R.drawable.defefornt, R.drawable.defendback));
        btnSpeed.setOnClickListener(v -> toggleFighter("Speed", 80, 25, new SpeedTyping(), R.drawable.speedfront, R.drawable.speedback));
        btnIntelligent.setOnClickListener(v -> toggleFighter("Intelligent", 90, 40, new IntelligenceTyping(), R.drawable.inteligens, R.drawable.inteligenback));

        // Start kamp knappen
        btnStart.setOnClickListener(v -> {
            if (BattleManager.PlayerTeam.size() == 3) {
                Intent intent = new Intent(this, Simple_Battle.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void toggleFighter(String name, int hp, int dmg, TypeClass type, int frontImg, int backImg) {
        int existingIndex = -1;

        for (int i = 0; i < BattleManager.PlayerTeam.size(); i++) {
            if (BattleManager.PlayerTeam.get(i).getName().equals(name)) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex != -1) {
            BattleManager.PlayerTeam.remove(existingIndex);
        } else {
            if (BattleManager.PlayerTeam.size() < 3) {
                // Nu gemmer vi også billed-ID'erne på helten
                BattleManager.PlayerTeam.add(new Fighter(name, hp, dmg, type, frontImg, backImg));
            } else {
                Toast.makeText(this, "You can only select 3 fighters!", Toast.LENGTH_SHORT).show();
            }
        }
        updateUI();
    }

    private void updateUI() {
        btnPower.setText("Power");
        btnDefence.setText("Defence");
        btnSpeed.setText("Speed");
        btnIntelligent.setText("Intelligent");

        for (int i = 0; i < BattleManager.PlayerTeam.size(); i++) {
            String name = BattleManager.PlayerTeam.get(i).getName();
            String statusText = name + " (Nr. " + (i + 1) + ")";
            
            if (name.equals("Power")) btnPower.setText(statusText);
            else if (name.equals("Defence")) btnDefence.setText(statusText);
            else if (name.equals("Speed")) btnSpeed.setText(statusText);
            else if (name.equals("Intelligent")) btnIntelligent.setText(statusText);
        }

        btnStart.setEnabled(BattleManager.PlayerTeam.size() == 3);
        if (BattleManager.PlayerTeam.size() == 3) {
            btnStart.setText("START BATTLE!");
        } else {
            btnStart.setText("Select 3 fighters (" + BattleManager.PlayerTeam.size() + "/3)");
        }
    }
}
