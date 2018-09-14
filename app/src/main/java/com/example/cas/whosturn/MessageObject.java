package com.example.cas.whosturn;

public class MessageObject {
    public String NAME_FIELD="Kishan";
    public Boolean WHOS_TURN=true;
    public int COUNTER=0;

    public MessageObject(int COUNTER) {
        this.COUNTER = COUNTER;
    }

    public MessageObject(Boolean WHOS_TURN) {
        this.WHOS_TURN = WHOS_TURN;
    }

    public MessageObject(String NAME_FIELD, Boolean WHOS_TURN, int COUNTER) {
        this.NAME_FIELD = NAME_FIELD;
        this.WHOS_TURN = WHOS_TURN;
        this.COUNTER = COUNTER;
    }

    public MessageObject(String NAME_FIELD) {
        this.NAME_FIELD = NAME_FIELD;
    }

    public MessageObject(String NAME_FIELD, Boolean WHOS_TURN) {
        this.NAME_FIELD = NAME_FIELD;
        this.WHOS_TURN = WHOS_TURN;
    }

    public MessageObject() {
    }
}
