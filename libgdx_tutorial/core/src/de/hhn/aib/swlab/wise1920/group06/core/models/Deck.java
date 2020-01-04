package de.hhn.aib.swlab.wise1920.group06.core.models;

import java.util.ArrayList;

public class Deck {

    private ArrayList<Card> stackList;


    //Getter and Setter
    public ArrayList<Card> getStackList() {
        return stackList;
    }

    public void setStackList(ArrayList<Card> stackList) {
        this.stackList = stackList;
    }

    public int getDeckSize(){
        return this.stackList.size();
    }

}
