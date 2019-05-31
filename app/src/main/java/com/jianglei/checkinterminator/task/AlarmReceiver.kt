package com.jianglei.checkinterminator.task

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

/**
 *@author longyi created on 19-5-30
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("longyi", "接收到定时提醒：" + Date().toString())
        val serviceIntent = Intent(context, ScheduleService::class.java)
        context!!.startService(serviceIntent)
    }
}