package com.jianglei.checkinterminator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jianglei.checkinterminator.location.LocationSelectActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLocate.setOnClickListener {
            val intent = Intent(this@MainActivity, LocationSelectActivity::class.java)
            startActivity(intent)
        }
    }
}
