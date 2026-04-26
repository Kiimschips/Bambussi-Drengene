package com.example.bambussi;

import com.example.bambussi.typings.TypeClass;
import java.io.Serializable;

public class Fighter extends TypeClass implements Serializable {
    private final String name;
    private int currentHealth;
    private final int maxHealth;
    private final int attackPower;
    private final TypeClass typing;

    public Fighter(String name, int maxHealth, int attackPower, TypeClass type) {
        this.name = name;
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.attackPower = attackPower;
        this.typing = type;
    }
    public String getName() { return name; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public TypeClass getTyping() { return typing; }
    public boolean isAlive() { return currentHealth > 0; }

    public void takeDamage(int amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }
}
