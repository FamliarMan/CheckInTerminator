package com.jianglei.checkinterminator.task

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption

/**
 *@author longyi created on 19-4-30
 */
class ScheduleService : Service() {
    private lateinit var mLocationClient: LocationClient
    private val mVibrator = application.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
//        mapInit()
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
}