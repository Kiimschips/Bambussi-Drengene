package com.example.bambussi.typingPackage;

import java.util.Arrays;

public class TypeClass {
    private float typeWeaknessMultiplier = 2f;
    private float typeResistanceMultiplier = 0.5f;
    private float totalWeaknessMultiplier = 1;

    protected String[] typeWeaknessArray;
    protected String[] typeResistanceArray;

    float getTypeMatchUp(String typing)
    {
        if (Arrays.asList(typeWeaknessArray).contains(typing))
        {
            totalWeaknessMultiplier *= typeWeaknessMultiplier;
        }
        else if (Arrays.asList(typeResistanceArray).contains(typing)) {
            totalWeaknessMultiplier *= typeResistanceMultiplier;
        }
        else {
            totalWeaknessMultiplier *= 1;
        }
        return totalWeaknessMultiplier;
    };
}
