package com.devrajnish.invoicegenerator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Integer.parseInt

class StockActivity : AppCompatActivity() {

    lateinit var s1_tv : TextView
    lateinit var s2_tv : TextView
    lateinit var s3_tv : TextView
    lateinit var s4_tv : TextView

    lateinit var s1_et : EditText
    lateinit var s2_et : EditText
    lateinit var s3_et : EditText
    lateinit var s4_et : EditText

    var s1 = 0;
    var s2= 0;
    var s3 = 0;
    var s4 = 0;

    lateinit var updatestockbtn : Button

    lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)

        supportActionBar?.hide()

        updatestockbtn = findViewById(R.id.updatestock);


        s1_tv = findViewById(R.id.s1_tv)
        s2_tv = findViewById(R.id.s2_tv)
        s3_tv = findViewById(R.id.s3_tv)
        s4_tv = findViewById(R.id.s4_tv)

        s1_et = findViewById(R.id.s1_update)
        s2_et = findViewById(R.id.s2_update)
        s3_et = findViewById(R.id.s3_update)
        s4_et = findViewById(R.id.s4_update)

        var sharedPreferences = getSharedPreferences("stock" , MODE_PRIVATE);
        s1_tv.text = sharedPreferences.getInt("s1" , 0).toString();
        s2_tv.text = sharedPreferences.getInt("s2" , 0).toString();
        s3_tv.text = sharedPreferences.getInt("s3" , 0).toString();
        s4_tv.text = sharedPreferences.getInt("s4" , 0).toString();

        updatestockbtn.setOnClickListener(View.OnClickListener {

            var editor = sharedPreferences.edit()
                if(s1_et.text.toString() != "")
                {
                    s1 = parseInt(s1_et.text.toString());
                    editor.putInt("s1" , s1);

                }





                 if(s2_et.text.toString() != "")
                {
                    s2 = parseInt(s2_et.text.toString());
                    editor.putInt("s2" , s2);

                }




            if(s3_et.text.toString() != "")
                {
                    s3 = parseInt(s3_et.text.toString());
                    editor.putInt("s3" , s3);

                }



            if(s4_et.text.toString() != "")
                {
                    s4 = parseInt(s4_et.text.toString());
                    editor.putInt("s4" , s4);

                }





                editor.commit()

                Toast.makeText(applicationContext,"Stock Updated successfully" , Toast.LENGTH_SHORT).show();
                intent = Intent(this,MainActivity::class.java);
                startActivity(intent);
                finish();


        })

        bottomNavigationView = findViewById(R.id.bottom_navigator)
        bottomNavigationView.selectedItemId = R.id.stock

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stock -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.rate -> {
                    startActivity(Intent(applicationContext, RateUpdate::class.java))
                    finish()
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })


    }
}