package com.example.bambussi;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.bambussi.typings.PowerTyping;
import com.example.bambussi.typings.SpeedTyping;
import com.example.bambussi.typings.DefenceTyping;
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
    private int currentFighterIndex = 0;
    private BattleState currentState;
    private BattleLogListener listener;
    private final Handler handler;

    public BattleManager(){
        currentPlayerFighter = PlayerTeam.get(0);
        if (EnemyTeam.isEmpty()){
            EnemyTeam.add(new Fighter("Power Enemy", 100, 30, new PowerTyping()));
            EnemyTeam.add(new Fighter("Speed Enemy", 80, 25, new SpeedTyping()));
            EnemyTeam.add(new Fighter("Defence Enemy", 150, 15, new DefenceTyping()));
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
                    log("your hero has fallen! " + currentPlayerFighter.getName() + " træder ind i kampen!");
                    currentState = BattleState.PLAYER_TURN;
                    log("it is your turn!");
                } else {
                    currentState = BattleState.DEFEAT;
                    log("Game Over: you have been DEFEATED!!.");
                }
            } else {
                currentState = BattleState.PLAYER_TURN;
                log("it is your turn!");
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
                log("A new enemy is approaching " + currentEnemyFighter.getName() + "!");
                currentState = BattleState.TRANSITION;
                handler.postDelayed(() -> {
                            currentState = BattleState.ENEMY_TURN;
                            enemyTurn();
                },1000);
                return;
            }
        }
        currentState = BattleState.VICTORY;
        log("Victory! You defeated the enemy!");
    }
    public void switchToFighter(int index) {
        if (currentState != BattleState.PLAYER_TURN) return;

        currentPlayerFighter = PlayerTeam.get(index);

        currentFighterIndex = index;
        log("You switched to " + currentPlayerFighter.getName() + "!");

        // Skift koster en tur
        currentState = BattleState.ENEMY_TURN;
        enemyTurn();
    }
}
