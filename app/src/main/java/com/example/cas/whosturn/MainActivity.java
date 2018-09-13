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

    private EditText nameET,textET;
    private TextView messageTV;

    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameET=(EditText)findViewById(R.id.name_et);
        textET=(EditText)findViewById(R.id.text_et);
        messageTV=(TextView)findViewById(R.id.message_tv);
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
                try {
                    messageObject=documentSnapshot.toObject(MessageObject.class);
                    messageTV.setText(messageObject.NAME_FIELD+": "+messageObject.TEXT_FIELD);
                } catch (NullPointerException e1) {
                    messageObject=new MessageObject("name","message");
                }

            } else {
                Toast.makeText(MainActivity.this, "Current data is null", Toast.LENGTH_SHORT).show();
            }
            }

    });

    }

    public void sendMessage(View view){
        try {
            messageObject=new MessageObject(nameET.getText().toString(),textET.getText().toString());
        } catch (NullPointerException e) {
            Toast.makeText(this, "Fields cant be Empty", Toast.LENGTH_SHORT).show();
        }
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
    }

    public void request(View view) {
    }
}
