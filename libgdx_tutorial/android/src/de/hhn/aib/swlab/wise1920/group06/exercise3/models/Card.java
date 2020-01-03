package de.hhn.aib.swlab.wise1920.group06.exercise3.models;

public class Card {

    private Value value;
    private Color color;

    public enum Color{
        RED (1),
        BLUE (2),
        GREEN (3),
        YELLOW (4),
        WILD (5);

        private int value;
        Color(int value){this.value = value;}

    }

    public enum Value{
        ZERO (0),
        ONE (1),
        TWO (2),
        THREE (3),
        FOUR (4),
        FIVE (5),
        SIX (6),
        SEVEN (7),
        EIGHT (8),
        NINE (9),
        SKIP (10),
        REVERSE(11),
        DRAWTWO (12),
        REGULAR (13),
        DRAWFOUR (14);

        private int value;
        Value(int value){this.value = value;}

    }


    public Card(String color, String val){
        this.color = Color.valueOf(color);
        this.value = Value.valueOf(val);
    }


    public Card(int cardColor, int cardValue){

        this.color = Color.values()[cardColor - 1];
        this.value = Value.values()[cardValue];

    }

    public String getCardResource(){
        String output = "";

        output += this.color.toString().toLowerCase() + this.value.toString().toLowerCase();

        return output;
        //String path = "sdcard/camera_app/name.jpg"; img.setImageDrawable(Drawable.createFromPath(path));
    }

    public String getButtonTag(){
        return this.color.toString().toUpperCase() + " " + this.value.toString().toUpperCase();
    }

    public Color getColor(){
        return this.color;
    }

    public Value getValue(){
        return this.value;
    }

    public boolean isWildCard(){
        return this.color == Color.values()[4];
    }

    public boolean isDrawTwo(){
        return this.value == Value.values()[12];
    }

    public boolean isSkip(){
        return this.value == Value.values()[10];
    }

    public boolean isDrawFour(){
        return this.value == Value.values()[14];
    }
    public boolean isNumberCard(){
        return this.value == Value.values()[13];
    }
    
}
