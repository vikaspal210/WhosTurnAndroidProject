package com.example.cas.whosturn;

public class MessageObject {
    public String NAME_FIELD;
    public String TEXT_FIELD;
    public Boolean WHOS_TURN=false;
    public int COUNTER=0;

    public MessageObject(String NAME_FIELD, String TEXT_FIELD) {
        this.NAME_FIELD = NAME_FIELD;
        this.TEXT_FIELD = TEXT_FIELD;
    }

    public MessageObject() {
    }
}
