package com.fenapnu.acquirentine_android;

public enum Corporation {

    SPARK("Spark"), NESTOR("Nestor"), ROVE("Rove"), FLEET("Fleet"),
    ETCH("Etch"), BOLT("Bolt"), ECHO("Echo");

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
}
