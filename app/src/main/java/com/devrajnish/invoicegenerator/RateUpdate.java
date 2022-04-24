package com.devrajnish.invoicegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RateUpdate extends AppCompatActivity {

    EditText ratep1,ratep2,ratep3,ratep4;
    int rp1 = 0,rp2 = 0,rp3 = 0,rp4 = 0;
    Button updaterate;

    int pp1,pp2,pp3,pp4;

    TextView pp1et,pp2et,pp3et,pp4et;

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_update);

        getSupportActionBar().hide();

        updaterate = findViewById(R.id.updaterateofupdateactivity);
        ratep1 = findViewById(R.id.p1_et_ofupadte);
        ratep2 = findViewById(R.id.p2_et_ofupdate);
        ratep3 = findViewById(R.id.p3_et_ofupdate);
        ratep4 = findViewById(R.id.p4_et_ofupdate);

        pp1et = findViewById(R.id.pp1_tv);
        pp2et = findViewById(R.id.pp2_tv);
        pp3et = findViewById(R.id.pp3_tv);
        pp4et = findViewById(R.id.pp4_tv);



        getpreviousrates();

        updaterate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratep1.getText().toString().isEmpty())
                    rp1=  0;
                else
                    rp1 = Integer.valueOf(ratep1.getText().toString());

                if(ratep2.getText().toString().isEmpty())
                    rp2=  0;
                else
                    rp2 = Integer.valueOf(ratep2.getText().toString());

                if(ratep3.getText().toString().isEmpty())
                    rp3=  0;
                else
                    rp3 = Integer.valueOf(ratep3.getText().toString());
                if(ratep4.getText().toString().isEmpty())
                    rp4=  0;
                else
                    rp4 = Integer.valueOf(ratep4.getText().toString());


                if((rp1==0 || rp2==0) || (rp3 ==0 || rp4 ==0))
                {
                    Toast.makeText(getApplicationContext(), "Fill all field", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("RateList",MODE_PRIVATE);

                    // Creating an Editor object to edit(write to the file)
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    // Storing the key and its value as the data fetched from edittext

                    myEdit.putInt("ratep1", Integer.parseInt(ratep1.getText().toString()));
                    myEdit.putInt("ratep2", Integer.parseInt(ratep2.getText().toString()));
                    myEdit.putInt("ratep3", Integer.parseInt(ratep3.getText().toString()));
                    myEdit.putInt("ratep4", Integer.parseInt(ratep4.getText().toString()));


                    // Once the changes have been made,
                    // we need to commit to apply those changes made,
                    // otherwise, it will throw an error
                    myEdit.commit();

                    Toast.makeText(getApplicationContext(), "Rate Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RateUpdate.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.rate);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.rate :
                    {

                        return true;
                    }

                    case R.id.home :
                    {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                    }
                    case R.id.stock:
                    {
                        startActivity(new Intent(getApplicationContext(),StockActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    }
                }
                return false;
            }
        });





    }

    private void getpreviousrates() {
        @SuppressLint("WrongConstant")
        SharedPreferences sh = getSharedPreferences("RateList", MODE_APPEND);


        pp1 = sh.getInt("ratep1", 0);
        pp2 = sh.getInt("ratep2", 0);
        pp3 = sh.getInt("ratep3", 0);
        pp4 = sh.getInt("ratep4", 0);

        pp1et.setText(String.valueOf( pp1));
        pp2et.setText(String.valueOf( pp2));
        pp3et.setText(String.valueOf( pp3));
        pp4et.setText(String.valueOf( pp4));

    }
}