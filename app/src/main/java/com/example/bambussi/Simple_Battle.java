package com.example.bambussi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.Serializable;
import java.util.ArrayList;

public class Simple_Battle extends AppCompatActivity {

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
        ArrayList<Fighter> myTeam = (ArrayList<Fighter>) getIntent().getSerializableExtra("MY_TEAM");

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

                // Re-enable the buttons only if it's your turn (P1)
                boolean isPlayerTurn = battleManager.getCurrentState() == BattleState.PLAYER_TURN;
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
        ArrayList<Fighter> team = battleManager.getPlayerTeam();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < team.size(); i++) {
            Fighter f = team.get(i);
            // Vis kun de andre helte som er i live
            if (i != battleManager.getCurrentFighterIndex() && f.isAlive()) {
                names.add(f.getName() + " (HP: " + f.getHealth() + ")");
                indices.add(i);
            }
        }

        if (names.isEmpty()) {
            battleManager.log("No other fighters are alive...");
            return;
        }

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
            this.enemy = new Fighter("Anderdingus", 50, 50, 15);
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
        public ArrayList<Fighter> getPlayerTeam() { return playerTeam; }
        public int getCurrentFighterIndex() { return currentFighterIndex; }

        public void switchToFighter(int index) {
            if (currentState != BattleState.PLAYER_TURN) return;
            
            player = playerTeam.get(index);
            currentFighterIndex = index;
            log("You switched to " + player.getName() + "!");
            
            // Skift koster en tur
            currentState = BattleState.ENEMY_TURN;
            enemyTurn();
        }

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
                    int nextAvailable = -1;
                    // Find den første helt der stadig er i live
                    for (int i = 0; i < playerTeam.size(); i++) {
                        if (playerTeam.get(i).isAlive()) {
                            nextAvailable = i;
                            break;
                        }
                    }

                    if (nextAvailable != -1) {
                        currentFighterIndex = nextAvailable;
                        player = playerTeam.get(currentFighterIndex);
                        log("Your fighter died. " + player.getName() + " The next one enters!");
                        currentState = BattleState.PLAYER_TURN;
                        log("It's your turn!");
                    } else {
                        currentState = BattleState.DEFEAT;
                        log("Game Over");
                    }
                } else {
                    currentState = BattleState.PLAYER_TURN;
                    log("its your turn");
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