package com.jianglei.checkinterminator.task

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.jianglei.checkinterminator.util.TaskUtils
import com.jianglei.girlshow.storage.TaskRecord
import java.util.*

/**
 *@author longyi created on 19-5-29
 */
class AlarmUtils {
    companion object {

        const val REQUEST_CODE = 1
        /**
         * 为
         */
        fun registryAlarm(application: Application, taskRecords: List<TaskRecord>?) {
            val intent = Intent(application, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                application,
                REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //首先取消之前的定时提醒
            alarmManager.cancel(pendingIntent)
            Log.d("longyi", "取消之前的定时提醒")
            if (taskRecords == null || taskRecords.isEmpty()) {
                return
            }
            val nextTime = TaskUtils.getNextRemindTime(taskRecords)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+nextTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+nextTime, pendingIntent)
            }
            Log.d("longyi", "设置提醒时间:" + Date(System.currentTimeMillis() + nextTime))
        }
    }
}