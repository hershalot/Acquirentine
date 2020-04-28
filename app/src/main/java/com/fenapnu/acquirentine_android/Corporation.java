package com.fenapnu.acquirentine_android;

import android.graphics.Color;

public enum Corporation {

    SPARK("Spark"), NESTOR("Nestor"), ROVE("Rove"), FLEET("Fleet"),
    ETCH("Etch"), BOLT("Bolt"), ECHO("Echo"), NONE("None");

    public final String label;


    Corporation(String corp) {
        this.label = corp;
    }


    public static Corporation valueOfLabel(String label) {
        for (Corporation e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }


//
//    public static Color colorForValue(String label){
//
//        Corporation c = valueOf(label);
//
//        switch (label) {
//
//            case NESTOR:
//
//            case SPARK:
//
//            case ETCH:
//
//            case FLEET:
//
//            case ROVE:
//
//            case ECHO:
//
//            case BOLT:
//
//
//        }
//
//
//
//    }



}
