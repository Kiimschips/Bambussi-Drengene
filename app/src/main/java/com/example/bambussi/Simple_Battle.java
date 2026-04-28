package com.example.bambussi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.Serializable;
import java.util.ArrayList;

public class Simple_Battle extends BaseMusicActivity {

    // UI Elements
    private BattleManager battleManager;
    private TextView battleLogTextView;
    private TextView playerHealthText;
    private TextView enemyHealthText;
    private Button attackButton;
    private Button changeFighterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simple_battle);

        // 1. Start kampmusikken
        startMusic(R.raw.bambusi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // link Java to XML
        battleLogTextView = findViewById(R.id.battleLogTextView);
        attackButton = findViewById(R.id.attackButton);
        changeFighterButton = findViewById(R.id.ChangeFighter);
        playerHealthText = findViewById(R.id.playerHealthText);
        enemyHealthText = findViewById(R.id.enemyHealthText);

        // 2. Aktiver Mute-knappen
        Button btnMute = findViewById(R.id.btnMute);
        if (btnMute != null) {
            btnMute.setOnClickListener(v -> toggleMute());
        }

        Log.d("TAG1", "onCreate: ");
        // Initialize Game Logic
        if (BattleManager.PlayerTeam != null && !BattleManager.PlayerTeam.isEmpty()) {
            Log.d("TAG2", "onCreate: ");
            battleManager = new BattleManager();
            Log.d("TAG3", "onCreate: ");
        } else {
            // Backup hvis noget går galt med overførslen
            Toast.makeText(this, "No Player Team Found", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAG4", "onCreate: ");
        // Initial UI state
        updateHealthUI();

        // Listen for game events?
        battleManager.setBattleLogListener(new BattleManager.BattleLogListener() {
            @Override
            public void onLogUpdated(String message) {
                // This updates the middle text
                battleLogTextView.setText(message);

                // This updates the HP numbers
                updateHealthUI();

                // Re-enable the button only if it's your turn (P1)
                boolean isPlayerTurn = battleManager.getCurrentState() == BattleManager.BattleState.PLAYER_TURN;
                attackButton.setEnabled(isPlayerTurn);
                changeFighterButton.setEnabled(isPlayerTurn);
            }
        });

        //  Handle Button Click
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                battleManager.playerAttack();
                // Immediately disable to prevent spamming during the enemy's turn
                attackButton.setEnabled(false);
                changeFighterButton.setEnabled(false);
            }
        });
        changeFighterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeFighterDialog();
            }
        });
    }
    private void showChangeFighterDialog() {
        ArrayList<Fighter> team = battleManager.PlayerTeam;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < team.size(); i++) {
            Fighter f = team.get(i);
            // Vis kun de andre helte som er i live
            //battleManager.getCurrentFighter()
            //i != battleManager.getPlayer &&
            if (battleManager.getPlayer().isAlive()) {
                names.add(f.getName() + " (HP: " + f.getCurrentHealth() + ")");
                indices.add(i);
            }
        }

        //if (names.isEmpty()) {
         //   battleManager.log("No other fighters are alive...");
          //  return;
        //}

        new AlertDialog.Builder(this)
                .setTitle("Choose your fighter (Uses turn)")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    battleManager.switchToFighter(indices.get(which));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateHealthUI() {
        if (battleManager != null && playerHealthText != null && enemyHealthText != null) {
            Fighter p = battleManager.getPlayer();
            Fighter e = battleManager.getEnemy();

            playerHealthText.setText("Your HP: " + p.getCurrentHealth() + "/" + p.getMaxHealth());
            enemyHealthText.setText("Enemy HP: " + e.getCurrentHealth() + "/" + e.getMaxHealth());
        }
    }
}