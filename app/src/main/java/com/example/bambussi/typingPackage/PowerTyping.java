package com.example.bambussi.typingPackage;

public class PowerTyping extends TypeClass {
    public String type = "Power";

    String[] typeWeaknessArray = {"Intelligence"};
    String[] typeResistanceArray = {"Defence"};

    @Override
    float getTypeMatchUp(String typing)
    {
        super.typeWeaknessArray = this.typeWeaknessArray;
        super.typeResistanceArray = this.typeResistanceArray;
        return super.getTypeMatchUp(typing);
    };
}