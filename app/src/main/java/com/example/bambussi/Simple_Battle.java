package com.example.bambussi;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simple_battle);
        ArrayList<Fighter> myTeam = (ArrayList<Fighter>) getIntent().getSerializableExtra("MY_TEAM");

        // Start kamp musik via arv
        startMusic(R.raw.bambusi);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // link Java to XML
        battleLogTextView = findViewById(R.id.battleLogTextView);
        attackButton = findViewById(R.id.attackButton);
        playerHealthText = findViewById(R.id.playerHealthText);
        enemyHealthText = findViewById(R.id.enemyHealthText);

        // Mute knap
        findViewById(R.id.btnMute).setOnClickListener(v -> toggleMute());

        // Initialize Game Logic
        if (myTeam != null && !myTeam.isEmpty()) {
            battleManager = new BattleManager(myTeam);
        } else {
            // Backup hvis noget går galt med overførslen
            ArrayList<Fighter> defaultTeam = new ArrayList<>();
            defaultTeam.add(new Fighter("Player 1", 100, 100, 25));
            battleManager = new BattleManager(defaultTeam);
        }

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
                boolean isPlayerTurn = battleManager.getCurrentState() == BattleState.PLAYER_TURN;
                attackButton.setEnabled(isPlayerTurn);
            }
        });

        //  Handle Button Click
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                battleManager.playerAttack();
                // Immediately disable to prevent spamming during the enemy's turn
                attackButton.setEnabled(false);
            }
        });
    }


    private void updateHealthUI() {
        if (battleManager != null && playerHealthText != null && enemyHealthText != null) {
            Fighter p = battleManager.getPlayer();
            Fighter e = battleManager.getEnemy();

            playerHealthText.setText("Your HP: " + p.getHealth() + "/" + p.getMaxHealth());
            enemyHealthText.setText("Enemy HP: " + e.getHealth() + "/" + e.getMaxHealth());
        }
    }




    public enum BattleState {
        PLAYER_TURN, ENEMY_TURN, VICTORY, DEFEAT
    }

    public static class Fighter implements Serializable {
        private final String name;
        private int health;
        private final int maxHealth;
        private final int attackPower;
        

        public Fighter(String name, int health, int maxHealth, int attackPower) {
            this.name = name;
            this.health = health;
            this.maxHealth = maxHealth;
            this.attackPower = attackPower;
        }

        public String getName() { return name; }
        public int getHealth() { return health; }
        public int getMaxHealth() { return maxHealth; }
        public int getAttackPower() { return attackPower; }
        public boolean isAlive() { return health > 0; }

        public void takeDamage(int amount) {
            health = Math.max(0, health - amount);
        }
    }

    public static class BattleManager {
        public interface BattleLogListener {
            void onLogUpdated(String message);
        }

        private final ArrayList<Fighter> playerTeam;
        private int currentFighterIndex = 0;
        private Fighter player;
        public BattleManager(ArrayList<Fighter> team){
            playerTeam = team;
            this.player = team.get(0);
            this.enemy = new Fighter("modstander", 50, 50, 15);
            currentState = BattleState.PLAYER_TURN;
            handler = new Handler(Looper.getMainLooper());
        }
        private final Fighter enemy;
        private BattleState currentState;
        private BattleLogListener listener;
        private final Handler handler;
        
        public Fighter getPlayer() { return player; }
        public Fighter getEnemy() { return enemy; }
        public BattleState getCurrentState() { return currentState; }

        public void setBattleLogListener(BattleLogListener listener) {
            this.listener = listener;
        }

        private void log(String message) {
            if (listener != null) listener.onLogUpdated(message);
        }

        public void playerAttack() {
            if (currentState != BattleState.PLAYER_TURN) return;

            enemy.takeDamage(player.getAttackPower());
            log("You hit the " + enemy.getName() + " for " + player.getAttackPower() + "!");

            checkWinCondition();
        }

        private void enemyTurn() {
            log(enemy.getName() + " is preparing to strike...");

            handler.postDelayed(() -> {
                player.takeDamage(enemy.getAttackPower());
                log(enemy.getName() + " attacked you for " + enemy.getAttackPower() + " damage!");
                
                if (!player.isAlive()) {
                    currentFighterIndex++;
                    if (currentFighterIndex < playerTeam.size()) {
                        player = playerTeam.get(currentFighterIndex);
                        log("Din helt faldt! " + player.getName() + " træder ind i kampen!");
                        currentState = BattleState.PLAYER_TURN;
                        log("Det er din tur!");
                    } else {
                        currentState = BattleState.DEFEAT;
                        log("Game Over: Hele dit hold er besejret.");
                    }
                } else {
                    currentState = BattleState.PLAYER_TURN;
                    log("Det er din tur!");
                }
            }, 1500);
        }

        private void checkWinCondition() {
            if (!enemy.isAlive()) {
                currentState = BattleState.VICTORY;
                log("Victory! You defeated " + enemy.getName() + "!");
            } else {
                currentState = BattleState.ENEMY_TURN;
                enemyTurn();
            }
        }
    }
}