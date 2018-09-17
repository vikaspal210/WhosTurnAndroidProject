package com.example.cas.whosturn;

public class MessageObject {
    public String NAMEOFTURN="";
    public Boolean WHOS_TURN=true;
    public int COUNTER=0;

    public MessageObject(int COUNTER) {
        this.COUNTER = COUNTER;
    }

    public MessageObject(Boolean WHOS_TURN) {
        this.WHOS_TURN = WHOS_TURN;
    }

    public MessageObject(String NAMEOFTURN, Boolean WHOS_TURN, int COUNTER) {
        this.NAMEOFTURN = NAMEOFTURN;
        this.WHOS_TURN = WHOS_TURN;
        this.COUNTER = COUNTER;
    }



    public MessageObject() {
    }
}
