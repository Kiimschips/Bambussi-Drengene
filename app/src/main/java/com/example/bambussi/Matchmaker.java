package com.example.bambussi;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bambussi.typings.DefenceTyping;
import com.example.bambussi.typings.IntelligenceTyping;
import com.example.bambussi.typings.PowerTyping;
import com.example.bambussi.typings.SpeedTyping;

public class Matchmaker extends AppCompatActivity {
    final static int POWER = 0, INTELLIGENCE = 1, SPEED = 2, DEFENCE = 3;

    public static void CheckTagValue(Context context, String value){
        switch (value){
            case "1":
                Log.d("LOAD_ENEMY", "Load Enemy Team 1");
                EnemyTeamConstructor(new int[]{POWER, SPEED, INTELLIGENCE});
                StartFight(context);
                break;
            case "2":
                Log.d("LOAD_ENEMY", "Load Enemy Team 2");
                EnemyTeamConstructor(new int[]{INTELLIGENCE, DEFENCE, INTELLIGENCE});
                StartFight(context);
                break;
            case "3":
                Log.d("LOAD_ENEMY", "Load Enemy Team 3");
                EnemyTeamConstructor(new int[]{SPEED, SPEED,SPEED});
                StartFight(context);
                break;
            default:
                Toast.makeText(context, "No Team Assigned To NFC", Toast.LENGTH_SHORT).show();
        }
    }

    private static void EnemyTeamConstructor(int[] fighters){
        BattleManager.EnemyTeam.clear();
        for (int fighter : fighters) {
            switch (fighter){
                case 0:
                    BattleManager.EnemyTeam.add(new Fighter("Power Enemy", 100, 30, new PowerTyping()));
                    break;
                case 1:
                    BattleManager.EnemyTeam.add(new Fighter("Intelligence Enemy", 100, 30, new IntelligenceTyping()));
                    break;
                case 2:
                    BattleManager.EnemyTeam.add(new Fighter("Speed Enemy", 100, 30, new SpeedTyping()));
                    break;
                case 3:
                    BattleManager.EnemyTeam.add(new Fighter("Defence Enemy", 100, 30, new DefenceTyping()));
                    break;
            }
        }
    }

    private static void StartFight(Context context){
        Intent intent = new Intent(context, ChampSelect.class);
        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}
