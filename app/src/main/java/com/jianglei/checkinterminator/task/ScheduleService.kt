package com.jianglei.checkinterminator.task

import android.app.*
import android.app.Notification.VISIBILITY_PUBLIC
import android.content.Context
import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.*
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.jianglei.checkinterminator.MyApplication
import com.jianglei.checkinterminator.R
import com.jianglei.checkinterminator.TaskListActivity
import com.jianglei.girlshow.storage.TaskRecord

/**
 *@author longyi created on 19-4-30
 */
class ScheduleService : Service() {
    private lateinit var mLocationClient: LocationClient
    private lateinit var mVibrator: Vibrator
    private lateinit var mModel: ScheduleServiceModel
    private var mTasks: List<TaskRecord>? = null
    private var mNotification: Notification? = null
    private val NOTIFICATION_ID = 1
    private var lastLatLng: LatLng? = null
    private var mBinder: LocalBinder? = null
    override fun onBind(intent: Intent?): IBinder? {
        if (mBinder == null) {
            mBinder = LocalBinder(mModel)
        }
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
//        mVibrator = val
        mapInit()
        mModel = ScheduleServiceModel(MyApplication.mApplication)
        mVibrator = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        //监听通知标题改变
        mModel.mNotificationTitle.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (sender == null) {
                    return
                }
                updateNotication((sender as ObservableField<String>).get())
            }
        })

        mModel.mRemindSwitch.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if((sender as ObservableBoolean).get()){
                    startVibrate()
                }else{
                    stopVibrate()
                }
            }
        })
        mModel.checkTask()
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
                lastLatLng = LatLng(location.latitude, location.longitude)
                mModel.mCurLatLng = lastLatLng
                mModel.checkTask()

            }
        })

        mLocationClient.start()
    }


    private fun startVibrate() {
        //停止1秒，开启震动10秒，然后又停止1秒，又开启震动10秒，重复.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(

                longArrayOf(1000, 3000, 1000, 3000),
                0
            )
            mVibrator.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            mVibrator.vibrate(longArrayOf(1000, 3000, 1000, 3000), 0)
        }

    }

    public fun stopVibrate() {
        mVibrator.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVibrate()
        mLocationClient.stop()
    }

    public fun startLocationService() {
        mNotification = createNotification(null)
        startForeground(NOTIFICATION_ID, mNotification)
    }

    private fun createNotification(contentText: String?): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent: PendingIntent =
            Intent(this, TaskListActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val content = contentText ?: getText(R.string.watching)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("1", "watch", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(this, "1")
                .setContentTitle(getText(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setTicker(getText(R.string.app_name))
                .build()
        } else {
            NotificationCompat.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setVisibility(VISIBILITY_PUBLIC)
                .setTicker(getText(R.string.app_name))
                .build()
        }

    }

    /**
     * 更新通知栏，[contentText]是通知栏显示的内容
     */
    private fun updateNotication(contentText: String?) {
        if (mNotification == null) {
            return
        }
        mNotification = createNotification(contentText)
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, mNotification!!)

    }


}