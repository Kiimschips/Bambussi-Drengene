package com.example.bambussi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;

public class Simple_Battle extends BaseMusicActivity {

    private BattleManager battleManager;
    private TextView battleLogTextView;
    private TextView playerHealthText;
    private TextView enemyHealthText;
    private Button attackButton;
    private Button changeFighterButton;
    
    private ImageView playerImageView;
    private ImageView enemyImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simple_battle);

        startMusic(R.raw.bambusi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        battleLogTextView = findViewById(R.id.battleLogTextView);
        attackButton = findViewById(R.id.attackButton);
        changeFighterButton = findViewById(R.id.ChangeFighter);
        playerHealthText = findViewById(R.id.playerHealthText);
        enemyHealthText = findViewById(R.id.enemyHealthText);
        playerImageView = findViewById(R.id.playerImageView);
        enemyImageView = findViewById(R.id.enemyImageView);

        ImageButton btnMute = findViewById(R.id.btnMute);
        if (btnMute != null) {
            btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            btnMute.setOnClickListener(v -> {
                toggleMute();
                btnMute.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
            });
        }

        if (BattleManager.PlayerTeam != null && !BattleManager.PlayerTeam.isEmpty()) {
            battleManager = new BattleManager();
        } else {
            Toast.makeText(this, "No fighters selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateBattleUI();

        battleManager.setBattleLogListener(new BattleManager.BattleLogListener() {
            @Override
            public void onLogUpdated(String message) {
                battleLogTextView.setText(message);
                updateBattleUI();

                // Trigger animation hvis nogen bliver ramt
                if (message.contains("damage to you")) {
                    performHitAnimation(playerImageView);
                } else if (message.contains("You hit")) {
                    performHitAnimation(enemyImageView);
                }

                BattleManager.BattleState state = battleManager.getCurrentState();
                
                if (state == BattleManager.BattleState.VICTORY || state == BattleManager.BattleState.DEFEAT) {
                    attackButton.setText("RETURN TO MENU");
                    attackButton.setEnabled(true);
                    attackButton.setVisibility(View.VISIBLE);
                    changeFighterButton.setVisibility(View.GONE);
                } else {
                    boolean isPlayerTurn = (state == BattleManager.BattleState.PLAYER_TURN);
                    attackButton.setEnabled(isPlayerTurn);
                    changeFighterButton.setEnabled(isPlayerTurn);
                    attackButton.setText("ATTACK");
                    changeFighterButton.setVisibility(View.VISIBLE);
                }
            }
        });

        attackButton.setOnClickListener(v -> {
            BattleManager.BattleState state = battleManager.getCurrentState();
            if (state == BattleManager.BattleState.VICTORY || state == BattleManager.BattleState.DEFEAT) {
                Intent intent = new Intent(Simple_Battle.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (state == BattleManager.BattleState.PLAYER_TURN) {
                attackButton.setEnabled(false);
                changeFighterButton.setEnabled(false);
                battleManager.playerAttack();
            }
        });

        changeFighterButton.setOnClickListener(v -> showChangeFighterDialog());
    }

    // SIMPEL SHAKE ANIMATION
    private void performHitAnimation(View view) {
        if (view == null) return;
        
        // Ryst til siden (Shake)
        view.animate()
            .translationX(20f)
            .setDuration(50)
            .withEndAction(() -> view.animate()
                .translationX(-20f)
                .setDuration(100)
                .withEndAction(() -> view.animate()
                    .translationX(0f)
                    .setDuration(50)
                    .start())
                .start())
            .start();

        // Blink effekt (Flash)
        view.setAlpha(0.5f);
        view.postDelayed(() -> view.setAlpha(1.0f), 150);
    }

    private void showChangeFighterDialog() {
        ArrayList<Fighter> team = BattleManager.PlayerTeam;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < team.size(); i++) {
            Fighter f = team.get(i);
            if (f.isAlive()) {
                names.add(f.getName() + " (HP: " + (int)f.getCurrentHealth() + ")");
                indices.add(i);
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Switch Fighter")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    battleManager.switchToFighter(indices.get(which));
                    updateBattleUI();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateBattleUI() {
        if (battleManager != null) {
            Fighter p = battleManager.getPlayer();
            Fighter e = battleManager.getEnemy();

            playerHealthText.setText("Your HP: " + (int)p.getCurrentHealth() + "/" + (int)p.getMaxHealth());
            enemyHealthText.setText("Enemy HP: " + (int)e.getCurrentHealth() + "/" + (int)e.getMaxHealth());

            if (playerImageView != null) {
                playerImageView.setImageResource(p.getBackImageResId());
            }
            if (enemyImageView != null) {
                enemyImageView.setImageResource(e.getFrontImageResId());
            }
        }
    }
}
