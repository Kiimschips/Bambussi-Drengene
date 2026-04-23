package com.example.bambussi.typingPackage;

public class DefenceTyping extends TypeClass {
    public String type = "Defence";

    String[] typeWeaknessArray = {"Power"};
    String[] typeResistanceArray = {"Speed"};

    @Override
    float getTypeMatchUp(String typing)
    {
        super.typeWeaknessArray = this.typeWeaknessArray;
        super.typeResistanceArray = this.typeResistanceArray;
        return super.getTypeMatchUp(typing);
    };
}