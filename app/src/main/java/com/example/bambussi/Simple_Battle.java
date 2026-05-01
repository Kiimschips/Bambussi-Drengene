package com.example.bambussi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import android.widget.ImageView;
import android.graphics.Color;
import android.graphics.PorterDuff;


public class Simple_Battle extends BaseMusicActivity {

    // UI Elements
    private BattleManager battleManager;
    private TextView battleLogTextView;
    private TextView playerHealthText;
    private TextView enemyHealthText;
    private ImageView playerImageView;
    private ImageView enemyImageView;
    private Button attackButton;
    private Button changeFighterButton;
    private Button returnToMainButton;


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
        playerImageView = findViewById(R.id.playerImageView);
        enemyImageView = findViewById(R.id.enemyImageView);
        returnToMainButton = findViewById(R.id.returnToMainButton);

        // 2. Aktiver Mute-knappen
        ImageButton btnMute = findViewById(R.id.btnMute);
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

                // Afspil animation hvis der er attackes
                if (message.contains("hit the")) {
                    playHitEffect(enemyImageView);
                } else if (message.contains("attacked you")) {
                    playHitEffect(playerImageView);
                }

                // Re-enable the button only if it's your turn (P1)
                boolean isPlayerTurn = battleManager.getCurrentState() == BattleManager.BattleState.PLAYER_TURN;
                attackButton.setEnabled(isPlayerTurn);
                changeFighterButton.setEnabled(isPlayerTurn);

                // Hvis kampen er slut, så vis menu-knappen
                if (battleManager.getCurrentState() == BattleManager.BattleState.VICTORY ||
                        battleManager.getCurrentState() == BattleManager.BattleState.DEFEAT) {
                    attackButton.setVisibility(View.GONE);
                    changeFighterButton.setVisibility(View.GONE);
                    returnToMainButton.setVisibility(View.VISIBLE);
                }
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
        returnToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

            // Opdater spillerens billede (back)
            if (p.getTyping().equals("Power")) playerImageView.setImageResource(R.drawable.powerback);
            else if (p.getTyping().equals("Speed")) playerImageView.setImageResource(R.drawable.speedback);
            else if (p.getTyping().equals("Defence")) playerImageView.setImageResource(R.drawable.defback);
            else if (p.getTyping().equals("Intelligence")) playerImageView.setImageResource(R.drawable.intiliback);

            // Opdater fjendens billede (front)
            if (e.getTyping().equals("Power")) enemyImageView.setImageResource(R.drawable.powerfront);
            else if (e.getTyping().equals("Speed")) enemyImageView.setImageResource(R.drawable.speedfront);
            else if (e.getTyping().equals("Defence")) enemyImageView.setImageResource(R.drawable.deffront);
            else if (e.getTyping().equals("Intelligence")) enemyImageView.setImageResource(R.drawable.intilifront);
        }
    }

    private void playHitEffect(final ImageView view) {
        if (view == null) return;
        view.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        view.animate().translationX(10f).setDuration(50).withEndAction(() -> 
            view.animate().translationX(-10f).setDuration(50).withEndAction(() -> 
                view.animate().translationX(0f).setDuration(50).withEndAction(view::clearColorFilter)
            )
        );
    }
}