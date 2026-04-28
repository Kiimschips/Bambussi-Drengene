package com.example.bambussi.typings;

public class DefenceTyping extends TypeClass {
    public String type = "Defence";

    String[] typeWeaknessArray = {"Power"};
    String[] typeResistanceArray = {"Speed"};

    @Override
    public float getTypeMatchUp(String typing)
    {
        super.typeWeaknessArray = this.typeWeaknessArray;
        super.typeResistanceArray = this.typeResistanceArray;
        return super.getTypeMatchUp(typing);
    };
}