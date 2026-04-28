package com.example.bambussi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bambussi.typings.PowerTyping;
import java.util.ArrayList;

public class BattleManager {
    public interface BattleLogListener {
        void onLogUpdated(String message);
    }
    public enum BattleState {
        PLAYER_TURN, ENEMY_TURN, VICTORY, DEFEAT
    }

    public static ArrayList<Fighter> PlayerTeam = new ArrayList<>();
    private Fighter currentPlayerFighter;
    public static ArrayList<Fighter> EnemyTeam = new ArrayList<>();
    private Fighter currentEnemyFighter;
    private int currentFighterIndex = 0;
    private BattleState currentState;
    private BattleLogListener listener;
    private final Handler handler;

    public BattleManager(){
        currentPlayerFighter = PlayerTeam.get(0);
        if (EnemyTeam.isEmpty()){
            EnemyTeam.add(new Fighter("Power", 100, 30, new PowerTyping()));
        }
        currentEnemyFighter = EnemyTeam.get(0);
        currentState = BattleState.PLAYER_TURN;
        handler = new Handler(Looper.getMainLooper());
    }
    public Fighter getPlayer() { return currentPlayerFighter; }
    public Fighter getEnemy() { return currentEnemyFighter; }
    public BattleState getCurrentState() { return currentState; }

    public void setBattleLogListener(BattleManager.BattleLogListener listener) {
        this.listener = listener;
    }

    private void log(String message) {
        if (listener != null) listener.onLogUpdated(message);
    }

    public void playerAttack() {
        if (currentState != BattleState.PLAYER_TURN) return;

        currentEnemyFighter.takeDamage(currentPlayerFighter);
        log("You hit the " + currentEnemyFighter.getName() + " for " + currentPlayerFighter.getAttackPower() + "!");

        checkWinCondition();
    }

    private void enemyTurn() {
        log(currentEnemyFighter.getName() + " is preparing to strike...");

        handler.postDelayed(() -> {
            currentPlayerFighter.takeDamage(currentEnemyFighter);
            log(currentEnemyFighter.getName() + " attacked you for " + currentEnemyFighter.getAttackPower() + " damage!");

            if (!currentPlayerFighter.isAlive()) {
                currentFighterIndex++;
                if (currentFighterIndex < PlayerTeam.size()) {
                    currentPlayerFighter = PlayerTeam.get(currentFighterIndex);
                    log("Din helt faldt! " + currentPlayerFighter.getName() + " træder ind i kampen!");
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
        if (currentEnemyFighter.isAlive()) {
            currentState = BattleState.ENEMY_TURN;
            enemyTurn();
            return;
        }
        Log.d("TAG", ""+EnemyTeam);
        for (Fighter enemy: EnemyTeam) {
            if (enemy.isAlive()){
                currentEnemyFighter = enemy;
                currentState = BattleState.ENEMY_TURN;
                enemyTurn();
                return;
            }
        }
        currentState = BattleState.VICTORY;
        log("Victory! You defeated the enemy!");
    }
}
