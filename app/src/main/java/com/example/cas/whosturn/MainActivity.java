package com.example.cas.whosturn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String COLLECTION_KEY="chat";
    private String DOCUMENT_KEY="message";
    private static final String KISHAN="Kishan Kushwaha";
    private static final String VIKAS="Vikas Pal";
    private MessageObject messageObject;
    private TextView whosTurnTV,askToConfirmTV;

    //document reference
    private DocumentReference documentReference;

    //for shared preferences
    public static Boolean isFirstRun;

    //Firebase Auth
    //declare instance of FirebaseAuth
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    //instance of MainActivity
    public static Activity instance;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize views
        whosTurnTV=(TextView)findViewById(R.id.whos_tv);
        askToConfirmTV=(TextView)findViewById(R.id.ask_to_confirm);
        //set click listener to buttons
        findViewById(R.id.confirm_button).setOnClickListener(this);
        findViewById(R.id.request_Button).setOnClickListener(this);
        findViewById(R.id.init_button).setOnClickListener(this);

        //initialize instance of Activity for finishing it
        instance=this;
        mAuth=FirebaseAuth.getInstance();

        //code to check if LoginActivity have run once if not runs LoginActivity then run MainActivity.class
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        //firestore document reference
        documentReference= FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY);
        //real time update
        realTimeUpdateListener();

        //set owner and partner
        currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){
            setOwner();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

                Task<DocumentSnapshot> task=documentReference.get();
                while (!task.isComplete()) {
                }
                documentSnapshot = task.getResult();

                messageObject=documentSnapshot.toObject(MessageObject.class);
                if (messageObject!=null) {
                    updateUI(messageObject);

                }else{
                    if (currentUser!=null) {
                        messageObject=new MessageObject("Kishan Kushawaha",true,0);
                    }else{
                        messageObject=new MessageObject("Should login first",true,0);
                    }
                    sendMessage(messageObject);
                }


            } else {
                Toast.makeText(MainActivity.this, "Current data is null", Toast.LENGTH_SHORT).show();
            }
            }

    });

    }//realTimeUpdateListener() END

    //update data on firestore
    private void sendMessage(final MessageObject mMessageObject){
        documentReference.set(mMessageObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                        updateUI(mMessageObject);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failure to Send Message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void request() {
        if(readFirestore().WHOS_TURN){
            sendMessage(new MessageObject(readFirestore().NAMEOFTURN,readFirestore().WHOS_TURN,1));
        }else{
            sendMessage(new MessageObject(readFirestore().NAMEOFTURN,readFirestore().WHOS_TURN,2));
        }
    }

    public void confirm() {
            if (readFirestore().WHOS_TURN) {
                sendMessage(new MessageObject(readFirestore().NAMEOFTURN,readFirestore().WHOS_TURN,3));
            } else {
                sendMessage(new MessageObject(readFirestore().NAMEOFTURN,readFirestore().WHOS_TURN,4));
            }
    }


    private void setWhosTurnTextView(MessageObject mMessageObject){
        if(messageObject!=null) {
            if (mMessageObject.WHOS_TURN) {
                whosTurnTV.setText(KISHAN);
            } else {
                whosTurnTV.setText(VIKAS);
            }
        }
    }

    private void updateUI(MessageObject updateUIMesssageObj){
        //for buttons visibility
        if (updateUIMesssageObj.COUNTER==0&&updateUIMesssageObj.NAMEOFTURN.equals(LoginActivity.getDefaults("OWNER",getApplicationContext()))){
            findViewById(R.id.confirm_button).setVisibility(View.GONE);
            findViewById(R.id.request_Button).setVisibility(View.VISIBLE);
            askToConfirmTV.setVisibility(View.GONE);
        }
        if (updateUIMesssageObj.COUNTER==0&&!updateUIMesssageObj.NAMEOFTURN.equals(LoginActivity.getDefaults("OWNER",getApplicationContext()))){
            findViewById(R.id.request_Button).setVisibility(View.GONE);
            askToConfirmTV.setVisibility(View.GONE);
        }

        if (updateUIMesssageObj.COUNTER==1&&!updateUIMesssageObj.NAMEOFTURN.equals(LoginActivity.getDefaults("OWNER",getApplicationContext()))){
            //show message and Confirm button
            askToConfirmTV.setVisibility(View.VISIBLE);
            findViewById(R.id.confirm_button).setVisibility(View.VISIBLE);
        }
        if (updateUIMesssageObj.COUNTER==2&&!updateUIMesssageObj.NAMEOFTURN.equals(LoginActivity.getDefaults("OWNER",getApplicationContext()))){
            //show message and Confirm button
            askToConfirmTV.setVisibility(View.VISIBLE);
            findViewById(R.id.confirm_button).setVisibility(View.VISIBLE);
        }
        //for updating turn value
        if (updateUIMesssageObj.COUNTER==4){
            findViewById(R.id.confirm_button).setVisibility(View.GONE);
            findViewById(R.id.request_Button).setVisibility(View.GONE);
            sendMessage(new MessageObject(KISHAN,true,0));
        }
        if (updateUIMesssageObj.COUNTER==3){
            findViewById(R.id.confirm_button).setVisibility(View.GONE);
            findViewById(R.id.request_Button).setVisibility(View.GONE);
            sendMessage(new MessageObject(VIKAS,false,0));
        }

        //to update ui TEXT according to data
        setWhosTurnTextView(readFirestore());
    }

    public void enterLoginActivity(View view) {
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }

    //set owner and partner
    private void setOwner(){
        //set OWNER to current user, so that don't have to do network connection always
        LoginActivity.setDefaults("OWNER", currentUser.getDisplayName(), getApplicationContext());

    }//setOwnerPartner() END

    //returns messageObject to read data from
    public MessageObject readFirestore(){
        documentReference= FirebaseFirestore.getInstance().collection(COLLECTION_KEY).document(DOCUMENT_KEY);
        MessageObject messageObj = null;
        Task<DocumentSnapshot> task=documentReference.get();
        while (!task.isComplete()) {
        }
            documentSnapshot = task.getResult();

            if (documentSnapshot.exists()) {
                messageObj = documentSnapshot.toObject(MessageObject.class);
            }
        return messageObj;
    }

    //may never need this
    /*private void toggleWhosTurn(){
        if (messageObject.WHOS_TURN){
                messageObject=new MessageObject(false);
                sendMessage(messageObject);
        }else{
                messageObject=new MessageObject(true);
                sendMessage(messageObject);
        }
    }*/
    //onClick implemented here
    @Override
    public void onClick(View v) {
        int i=v.getId();
        if(i==R.id.request_Button){
            request();
        }else if(i==R.id.confirm_button){
            confirm();
        }else if (i==R.id.init_button){
            sendMessage(new MessageObject(KISHAN,true,0));
        }

    }

}
