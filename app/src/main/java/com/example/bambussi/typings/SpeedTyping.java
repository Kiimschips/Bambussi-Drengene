package com.example.bambussi.typings;

public class SpeedTyping extends TypeClass {
    public String type = "Speed";

    String[] typeWeaknessArray = {"Defence"};
    String[] typeResistanceArray = {"Intelligence"};

    @Override
    public float getTypeMatchUp(String typing)
    {
        super.typeWeaknessArray = this.typeWeaknessArray;
        super.typeResistanceArray = this.typeResistanceArray;
        return super.getTypeMatchUp(typing);
    };
}