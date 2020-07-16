package com.example.covidbeds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class InfoScreen extends AppCompatActivity {
    private String finalData = "";
    private FirebaseDatabase mDatabase;
    private LinearLayout linearlayout;
    private String userlist = "";

    private boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);

        mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myNewRef = mDatabase.getReference("users");
        final DatabaseReference detectChangeRef = mDatabase.getReference();

        detectChangeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                linearlayout.removeAllViews();

                myNewRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        userlist = dataSnapshot.getValue(String.class);
                        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);
                        linearlayout.removeAllViews();
                        redistData(userlist);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Toast.makeText(InfoScreen.this, "Data Read failed: please contact support if you get this error",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                //redistData(userlist);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(InfoScreen.this, "Data Read failed: please contact support if you get this error",
                        Toast.LENGTH_SHORT).show();
            }
        });




        ImageView update = (ImageView) findViewById(R.id.updateData);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(InfoScreen.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    void redistData(String userlist){
        //usernames are still parsed space separated
        String[] splitList = userlist.split("\\s+");
        //Toast.makeText(MainActivity.this, splitList[0], Toast.LENGTH_SHORT).show();
        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearlayout.removeAllViews();

        for(String userName : splitList){
            DatabaseReference userRef = mDatabase.getReference(userName);

            userRef.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    finalData = dataSnapshot.getValue(String.class);
                    String[] split = finalData.split("%");

                    GridLayout gridlayout = new GridLayout(getApplicationContext());
                    gridlayout.setUseDefaultMargins(false);
                    gridlayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
                    gridlayout.setRowOrderPreserved(false);
                    gridlayout.setColumnCount(4);

                    //GridLayout parameters
                    Point size = new Point();
                    getWindowManager().getDefaultDisplay().getSize(size);
                    int screenWidth = size.x;
                    int screenHeight = size.y;
                    int halfScreenWidth = (int) (screenWidth * 0.5);
                    int quarterScreenWidth = (int) (halfScreenWidth * 0.5);

                    TextView name = new TextView(getApplicationContext());
                    //split[0].replace("_", "\\s+");
                    name.setText(split[0]);
                    name.setGravity(Gravity.CENTER);
                    name.setBackgroundResource(R.drawable.ic_leftendentry_01);
                    name.setTextColor(Color.rgb(255, 255, 255));
                    name.setTextSize(16);
                    GridLayout.LayoutParams param1 = new GridLayout.LayoutParams();
                    param1.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    param1.width = quarterScreenWidth;
                    param1.rightMargin = 0;
                    param1.topMargin = 5;
                    param1.setGravity(Gravity.CENTER);
                    param1.columnSpec = GridLayout.spec(0);
                    param1.rowSpec = GridLayout.spec(0);
                    name.setLayoutParams(param1);

                    TextView normalBeds = new TextView(getApplicationContext());
                    normalBeds.setText(split[1]);
                    normalBeds.setGravity(Gravity.CENTER);
                    normalBeds.setBackgroundResource(R.drawable.ic_middleentry_01);
                    normalBeds.setTextColor(Color.rgb(0, 0, 0));
                    normalBeds.setTextSize(16);
                    GridLayout.LayoutParams param2 = new GridLayout.LayoutParams();
                    param2.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    param2.width = quarterScreenWidth;
                    param2.rightMargin = 0;
                    param2.topMargin = 5;
                    param2.setGravity(Gravity.CENTER);
                    param2.columnSpec = GridLayout.spec(1);
                    param2.rowSpec = GridLayout.spec(0);
                    normalBeds.setLayoutParams(param2);

                    TextView icuBeds = new TextView(getApplicationContext());
                    icuBeds.setText(split[2]);
                    icuBeds.setGravity(Gravity.CENTER);
                    icuBeds.setBackgroundResource(R.drawable.ic_middleentry_01);
                    icuBeds.setTextColor(Color.rgb(0, 0, 0));
                    icuBeds.setTextSize(16);
                    GridLayout.LayoutParams param3 = new GridLayout.LayoutParams();
                    param3.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    param3.width = quarterScreenWidth;
                    param3.rightMargin = 0;
                    param3.topMargin = 5;
                    param3.setGravity(Gravity.CENTER);
                    param3.columnSpec = GridLayout.spec(2);
                    param3.rowSpec = GridLayout.spec(0);
                    icuBeds.setLayoutParams(param3);

                    TextView dialysisBeds = new TextView(getApplicationContext());
                    dialysisBeds.setText(split[3]);
                    dialysisBeds.setGravity(Gravity.CENTER);
                    dialysisBeds.setBackgroundResource(R.drawable.ic_rightendentry_01);
                    dialysisBeds.setTextColor(Color.rgb(0, 0, 0));
                    dialysisBeds.setTextSize(16);
                    GridLayout.LayoutParams param4 = new GridLayout.LayoutParams();
                    param4.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    param4.width = quarterScreenWidth;
                    param4.rightMargin = 0;
                    param4.topMargin = 5;
                    param4.setGravity(Gravity.CENTER);
                    param4.columnSpec = GridLayout.spec(3);
                    param4.rowSpec = GridLayout.spec(0);
                    dialysisBeds.setLayoutParams(param4);

                    TextView lastUpdated = new TextView(getApplicationContext());
                    lastUpdated.setText("Last Updated by ".concat(split[0]).concat(" at ").concat(split[4]));
                    lastUpdated.setGravity(Gravity.RIGHT);
                    lastUpdated.setTextSize(10);
                    lastUpdated.setTextColor(Color.rgb(0,0,0));

                    gridlayout.addView(name, 0);
                    gridlayout.addView(normalBeds, 1);
                    gridlayout.addView(icuBeds, 2);
                    gridlayout.addView(dialysisBeds, 3);

                    linearlayout.addView(gridlayout);
                    linearlayout.addView(lastUpdated);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(InfoScreen.this, "userRead failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}