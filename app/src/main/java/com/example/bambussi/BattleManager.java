package com.example.bambussi;

import android.os.Handler;
import android.os.Looper;
import com.example.bambussi.typings.PowerTyping;
import com.example.bambussi.typings.SpeedTyping;
import com.example.bambussi.typings.IntelligenceTyping;
import java.util.ArrayList;

public class BattleManager {
    public interface BattleLogListener {
        void onLogUpdated(String message);
    }
    public enum BattleState {
        PLAYER_TURN, ENEMY_TURN, TRANSITION, VICTORY, DEFEAT
    }

    public static ArrayList<Fighter> PlayerTeam = new ArrayList<>();
    private Fighter currentPlayerFighter;
    public static ArrayList<Fighter> EnemyTeam = new ArrayList<>();
    private Fighter currentEnemyFighter;
    
    private BattleState currentState;
    private BattleLogListener listener;
    private final Handler handler;

    public BattleManager(){
        handler = new Handler(Looper.getMainLooper());
        
        // 1. Give some default enemies if none exist
        if (EnemyTeam.isEmpty()){
            EnemyTeam.add(new Fighter("Power Enemy", 100, 30, new PowerTyping(), R.drawable.powerfront, R.drawable.powerback));
            EnemyTeam.add(new Fighter("Speed Enemy", 80, 25, new SpeedTyping(), R.drawable.speedfront, R.drawable.speedback));
            EnemyTeam.add(new Fighter("Intel Enemy", 90, 40, new IntelligenceTyping(), R.drawable.inteligens, R.drawable.inteligenback));
        }

        // 2. ALWAYS reset health for everyone at start of battle
        for (Fighter f : PlayerTeam) f.resetHealth();
        for (Fighter f : EnemyTeam) f.resetHealth();

        // 3. Set initial fighters
        currentPlayerFighter = PlayerTeam.get(0);
        currentEnemyFighter = EnemyTeam.get(0);
        
        currentState = BattleState.PLAYER_TURN;
    }
    
    public Fighter getPlayer() { return currentPlayerFighter; }
    public Fighter getEnemy() { return currentEnemyFighter; }
    public BattleState getCurrentState() { return currentState; }

    public void setBattleLogListener(BattleLogListener listener) {
        this.listener = listener;
    }

    private void log(String message) {
        if (listener != null) listener.onLogUpdated(message);
    }

    public void playerAttack() {
        if (currentState != BattleState.PLAYER_TURN) return;

        currentEnemyFighter.takeDamage(currentPlayerFighter);
        log("You hit " + currentEnemyFighter.getName() + " and dealt " + (int)currentPlayerFighter.getAttackPower() + " damage!");

        checkWinCondition();
    }

    private void enemyTurn() {
        if (currentState != BattleState.ENEMY_TURN) return;
        
        log(currentEnemyFighter.getName() + " is attacking...");

        handler.postDelayed(() -> {
            currentPlayerFighter.takeDamage(currentEnemyFighter);
            log(currentEnemyFighter.getName() + " dealt " + (int)currentEnemyFighter.getAttackPower() + " damage to you!");

            if (!currentPlayerFighter.isAlive()) {
                switchToNextPlayerFighter();
            } else {
                currentState = BattleState.PLAYER_TURN;
                log("It's your turn!");
            }
        }, 1000);
    }

    private void switchToNextPlayerFighter() {
        Fighter next = null;
        for (Fighter f : PlayerTeam) {
            if (f.isAlive()) {
                next = f;
                break;
            }
        }

        if (next != null) {
            currentPlayerFighter = next;
            currentState = BattleState.PLAYER_TURN; // SKIFT TUR FØR LOG
            log("Your fighter fell! " + currentPlayerFighter.getName() + " enters the battle!");
        } else {
            currentState = BattleState.DEFEAT;
            log("Game Over: Your team was defeated!");
        }
    }

    private void checkWinCondition() {
        if (currentEnemyFighter.isAlive()) {
            currentState = BattleState.ENEMY_TURN;
            enemyTurn();
            return;
        }

        // Enemy died!
        currentState = BattleState.TRANSITION;
        log(currentEnemyFighter.getName() + " has been defeated!");

        // Find next living enemy
        Fighter next = null;
        for (Fighter e : EnemyTeam) {
            if (e.isAlive()) {
                next = e;
                break;
            }
        }

        if (next != null) {
            final Fighter nextEnemy = next;
            handler.postDelayed(() -> {
                currentEnemyFighter = nextEnemy;
                currentState = BattleState.PLAYER_TURN; // SKIFT TUR FØR LOG
                log("A new opponent: " + currentEnemyFighter.getName() + "!");
            }, 1000);
        } else {
            currentState = BattleState.VICTORY;
            log("Victory! All enemies defeated!");
        }
    }
    
    public void switchToFighter(int index) {
        if (currentState != BattleState.PLAYER_TURN) return;
        Fighter selected = PlayerTeam.get(index);
        if (selected == currentPlayerFighter || !selected.isAlive()) return;

        currentState = BattleState.ENEMY_TURN; // LÅS KNAPPER MED DET SAMME
        currentPlayerFighter = selected;
        log("Switched to " + currentPlayerFighter.getName());
        enemyTurn();
    }
}
