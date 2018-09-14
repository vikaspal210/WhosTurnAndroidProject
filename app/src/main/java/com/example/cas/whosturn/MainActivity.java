package com.example.cas.whosturn;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private String COLLECTION_KEY="chat";
    private String DOCUMENT_KEY="message";
    private MessageObject messageObject;

    private TextView whosTurnTV;

    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize views
        whosTurnTV=(TextView)findViewById(R.id.whos_tv);
        //firestore document reference
        documentReference= FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY);
        realTimeUpdateListener();
    }

    private void realTimeUpdateListener(){
    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
            if (e != null) {
                System.err.println("Listen failed: " + e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {

                if (messageObject!=null) {
                    messageObject=documentSnapshot.toObject(MessageObject.class);
                    whosTurn(messageObject);
                }else{
                    messageObject=new MessageObject("Kishan",true,0);
                    sendMessage();
                }



            } else {
                Toast.makeText(MainActivity.this, "Current data is null", Toast.LENGTH_SHORT).show();
            }
            }

    });

    }

    private void sendMessage(){
        documentReference.set(messageObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failure to Send Message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void confirm(View view) {
        messageObject=new MessageObject(2);
        sendMessage();
    }

    public void request(View view) {
        messageObject=new MessageObject(1);
        sendMessage();
    }

    private void whosTurn(MessageObject mMessageObject){
        if(mMessageObject.WHOS_TURN){
            mMessageObject=new MessageObject("Saurabh",false);
            sendMessage();
            whosTurnTV.setText(mMessageObject.NAME_FIELD);
        }else{
            mMessageObject=new MessageObject("Kishan",true);
            sendMessage();
            whosTurnTV.setText(mMessageObject.NAME_FIELD);
        }
    }
    private void updateTurn(){
        if (messageObject.WHOS_TURN){
            if (messageObject.COUNTER==2){
                messageObject=new MessageObject(false);
                sendMessage();
            }
        }else{
            if (messageObject.COUNTER==2){
                messageObject=new MessageObject(true);
                sendMessage();
            }
        }



    }
}
