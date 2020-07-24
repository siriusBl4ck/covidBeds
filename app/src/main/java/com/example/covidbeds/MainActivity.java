package com.example.covidbeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ImageView login;
    private ImageView update;
    private Button passwordReset;

    private EditText username;
    private EditText password;
    private EditText data;
    private TextView displayData;
    private TextView greetText;
    private EditText nonIcuBeds;
    private EditText icuBeds;
    private EditText dialysisBeds;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private String value = null;
    private String user = null;
    private String finalData = "";
    private String currentName = null;
    private String unParsedDateTime = null;
    private String currentDateTime = "Last Updated: ";

    private boolean dontDoitFirst = false;

    @Override
    public void onBackPressed() {
        //Restart main window:
        Intent intent = new Intent(MainActivity.this, InfoScreen.class);
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }

        // Read from the database

        login = (ImageView) findViewById(R.id.login_ui);
        update = (ImageView) findViewById(R.id.update_ui);
        passwordReset = (Button) findViewById(R.id.passwordReset);

        username = (EditText) findViewById(R.id.username_ui);
        password = (EditText) findViewById(R.id.password_ui);

        nonIcuBeds = (EditText) findViewById(R.id.nonIcuBeds);
        icuBeds = (EditText) findViewById(R.id.icuBeds);
        dialysisBeds = (EditText) findViewById(R.id.dialysisBeds);

        greetText = (TextView) findViewById(R.id.greetText);

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myNewRef = mDatabase.getReference("users");

        myNewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String userlist = dataSnapshot.getValue(String.class);

                if (dontDoitFirst){
                    redistData(userlist);
                }
                dontDoitFirst = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(MainActivity.this, "Data Read failed: please contact support if you get this error",
                        Toast.LENGTH_SHORT).show();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                signIn();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getCurrentDateTime();
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendPasswordResetEmail();
            }
        });
    }

    void sendPasswordResetEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (username.getText().toString().length() != 0) {
            String emailAddress = username.getText().toString();
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "an email was sent to your registered email address!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Invalid email address!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(MainActivity.this, "Please enter a registered email!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void signIn(){
        if (username.getText().toString().length() != 0 && password.getText().toString().length() != 0) {
            Toast.makeText(MainActivity.this, "signing in...",
                    Toast.LENGTH_SHORT).show();
            String email = username.getText().toString();
            String pass = password.getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "You have signed in successfully!",
                                        Toast.LENGTH_SHORT).show();
                                String userStuff = username.getText().toString().replace("@gmail.com", "");
                                redistData(userStuff);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Sign in failed!\nEither your uesrname or password is incorrect!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
        else{
            Toast.makeText(MainActivity.this, "Please enter a valid email and password!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void updateDataV2(){
        if (mAuth.getCurrentUser() != null) {
            String user = username.getText().toString().replace("@gmail.com", "");

            finalData = "";
            finalData = finalData.concat(currentName.concat("%"));

            finalData = finalData.concat(nonIcuBeds.getText().toString().concat("%"));
            finalData = finalData.concat(icuBeds.getText().toString().concat("%"));
            finalData = finalData.concat(dialysisBeds.getText().toString().concat("%"));
            finalData = finalData.concat(currentDateTime);

            mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference(user);

            myRef.setValue(finalData);
            Toast.makeText(MainActivity.this, "Data updated successfully!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainActivity.this, "Please sign in first!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void redistData(String userlist){
        String[] splitList = userlist.split("\\s+");
        //Toast.makeText(MainActivity.this, splitList[0], Toast.LENGTH_SHORT).show();
        finalData = "";
        if (mAuth.getCurrentUser() != null) {
            for (String userName : splitList) {
                //Toast.makeText(MainActivity.this, userName, Toast.LENGTH_SHORT).show();
                DatabaseReference userRef = mDatabase.getReference(userName);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        finalData = dataSnapshot.getValue(String.class);

                        String[] parsedData = finalData.split("%");

                        currentName = parsedData[0];
                        String newGreetText = "Welcome, "+ parsedData[0] + "!";
                        greetText.setText(newGreetText);
                        nonIcuBeds.setText(parsedData[1]);
                        icuBeds.setText(parsedData[2]);
                        dialysisBeds.setText(parsedData[3]);

                        //displayData.setText(finalData);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(MainActivity.this, "userRead failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    void getCurrentDateTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.google.com/search?client=ubuntu&channel=fs&q=current+date+and+time+ist&ie=utf-8&oe=utf-8").get();
                    unParsedDateTime = doc.body().text();


                } catch (IOException e) {
                    ;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int index = unParsedDateTime.indexOf("Search Results Local Time");
                        currentDateTime = unParsedDateTime.substring(index + 26, index + 50);
                        updateDataV2();
                    }
                });
            }
        }).start();
    }


}