package com.jianglei.checkinterminator.task

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.synchronization.DisplayOptions
import com.jianglei.checkinterminator.MainActivity
import com.jianglei.checkinterminator.R

/**
 *@author longyi created on 19-4-30
 */
class ScheduleService : Service() {
    private lateinit var mLocationClient: LocationClient
    private lateinit var mVibrator: Vibrator
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
//        mVibrator = val
        mapInit()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationService()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun mapInit() {
        //定位初始化
        mLocationClient = LocationClient(this)

        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setIsNeedAddress(true)
        option.setScanSpan(20000)

        //设置locationClientOption
        mLocationClient.locOption = option

        //注册LocationListener监听器
        mLocationClient.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location == null) {
                    return
                }

                Log.d(
                    "longyi",
                    "lat:" + location.latitude + "  lon:" + location.longitude + " add:" + location.addrStr
                )

            }
        })

        mLocationClient.start()
    }


    private fun startVibrate() {
        //停止1秒，开启震动10秒，然后又停止1秒，又开启震动10秒，重复.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(

                longArrayOf(1000, 10000, 1000, 10000),
                0
            )
            mVibrator.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            mVibrator.vibrate(longArrayOf(1000, 10000, 1000, 10000), 0)
        }

    }

    public fun stopVibrate() {
        mVibrator.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopVibrate()
        mLocationClient.stop()
    }

    public fun startLocationService() {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("1", "watch", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(this, "1")
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.watching))
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.app_name))
                .build()
        } else {
            NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.watching))
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.app_name))
                .build()
        }
        startForeground(1, notification)
    }

    private fun isRange() {
    }
}