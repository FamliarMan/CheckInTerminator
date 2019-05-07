package com.jianglei.checkinterminator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import com.jianglei.checkinterminator.location.LocationSelectActivity
import com.jianglei.checkinterminator.task.ScheduleService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLocate.setOnClickListener {
            val intent = Intent(this@MainActivity, LocationSelectActivity::class.java)
            startActivity(intent)
        }
        btnStart.setOnClickListener {
            startTask()
        }
        btnStop.setOnClickListener {
            val serviceIntent = Intent(this, ScheduleService::class.java)
            stopService(serviceIntent)
        }
    }

    fun startTask() {
        val serviceIntent = Intent(this, ScheduleService::class.java)
        startService(serviceIntent)
    }
}
