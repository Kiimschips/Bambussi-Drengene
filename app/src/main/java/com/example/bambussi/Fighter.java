package com.example.bambussi;

import com.example.bambussi.typings.DefenceTyping;
import com.example.bambussi.typings.IntelligenceTyping;
import com.example.bambussi.typings.PowerTyping;
import com.example.bambussi.typings.SpeedTyping;
import com.example.bambussi.typings.TypeClass;
import java.io.Serializable;

public class Fighter extends TypeClass implements Serializable {
    private final String name;
    private float currentHealth;
    private final float maxHealth;
    private final int attackPower;
    private final TypeClass typing;
    private final int frontImageResId;
    private final int backImageResId;

    public Fighter(String name, int maxHealth, int attackPower, TypeClass type, int frontImageResId, int backImageResId) {
        this.name = name;
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.attackPower = attackPower;
        this.typing = type;
        this.frontImageResId = frontImageResId;
        this.backImageResId = backImageResId;
    }
    
    public String getName() { return name; }
    public float getCurrentHealth() { return currentHealth; }
    public float getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public int getFrontImageResId() { return frontImageResId; }
    public int getBackImageResId() { return backImageResId; }

    public String getTyping() {
        if (typing.toString().contains("Power")){
            return "Power";
        }
        else if (typing.toString().contains("Intelligence")){
            return "Intelligence";
        }
        else if (typing.toString().contains("Speed")) {
            return "Speed";
        }
        else if (typing.toString().contains("Defence")) {
            return "Defence";
        }
        return "addø";
    }
    public boolean isAlive() { return currentHealth > 0; }

    private float CalculateDamageMultiplier(Fighter damager){
        float dmgMultiplier = 1;
        switch (getTyping()){
            case "Power":
                dmgMultiplier = new PowerTyping().getTypeMatchUp(damager.getTyping());
                break;
            case "Speed":
                dmgMultiplier = new SpeedTyping().getTypeMatchUp(damager.getTyping());
                break;
            case "Defence":
                dmgMultiplier = new DefenceTyping().getTypeMatchUp(damager.getTyping());
                break;
            case "Intelligence":
                dmgMultiplier = new IntelligenceTyping().getTypeMatchUp(damager.getTyping());
                break;
        }
        return dmgMultiplier;
    }

    public void takeDamage(Fighter damager) {
        float damage = damager.attackPower * CalculateDamageMultiplier(damager);

        currentHealth = Math.max(0, currentHealth - damage);
    }

    public void resetHealth() {
        this.currentHealth = this.maxHealth;
    }
}
