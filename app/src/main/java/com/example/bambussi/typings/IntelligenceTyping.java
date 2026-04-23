package com.example.bambussi.typings;

public class IntelligenceTyping extends TypeClass {
    public String type = "Intelligence";

    String[] typeWeaknessArray = {"Speed"};
    String[] typeResistanceArray = {"Power"};

    @Override
    float getTypeMatchUp(String typing)
    {
        super.typeWeaknessArray = this.typeWeaknessArray;
        super.typeResistanceArray = this.typeResistanceArray;
        return super.getTypeMatchUp(typing);
    };
}