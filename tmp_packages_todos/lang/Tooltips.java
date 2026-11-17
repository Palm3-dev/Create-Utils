package com.palm3.createutils.foundation.lang;

public enum Tooltips {
    SUMMARY(".tooltip.summary"),
    BEHAVIOUR(".tooltip.behaviour"),
    CONDITION(".tooltip.condition");

    public final String keyPiece;

    Tooltips(String keyPiece) {
        this.keyPiece = keyPiece;
    }
}
