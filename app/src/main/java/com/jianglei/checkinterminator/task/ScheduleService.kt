package com.jianglei.checkinterminator.task

import android.app.*
import android.app.Notification.VISIBILITY_PUBLIC
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.inner.Point
import com.baidu.mapapi.synchronization.DisplayOptions
import com.baidu.mapapi.utils.SpatialRelationUtil
import com.jianglei.checkinterminator.MainActivity
import com.jianglei.checkinterminator.R
import com.jianglei.checkinterminator.TaskListActivity
import com.jianglei.checkinterminator.TaskViewModel
import com.jianglei.checkinterminator.util.TaskUtils
import com.jianglei.girlshow.storage.DataStorage
import com.jianglei.girlshow.storage.TaskRecord
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.notificationManager

/**
 *@author longyi created on 19-4-30
 */
class ScheduleService : Service() {
    private lateinit var mLocationClient: LocationClient
    private lateinit var mVibrator: Vibrator
    private lateinit var taskViewModel: TaskViewModel
    private var mTasks: List<TaskRecord>? = null
    private var mNotification: Notification? = null
    private val NOTIFICATION_ID = 1
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
//        mVibrator = val
        mapInit()
        taskViewModel = TaskViewModel()
        taskViewModel.getTasks().observeForever {
            mTasks = it

        }
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
                if (mTasks == null || mTasks!!.isEmpty()) {
                    //没有任何任务，直接停止服务
                    Log.d("longyi","没有设置任何列表，停止监控服务")
                    stopSelf()
                }
                val readyRunningTaskNum = checkTask(mTasks!!, LatLng(location.latitude, location.longitude))
                if (readyRunningTaskNum == 0) {
                    return
                }
                updateNotication(readyRunningTaskNum)

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
     * 更新通知栏，[needRunTaskCnt]是需要执行的任务数量
     */
    private fun updateNotication(needRunTaskCnt: Int) {
        if (mNotification == null) {
            return
        }
        val text = if (needRunTaskCnt == 0) {
            getText(R.string.watching)
        } else {
            getString(R.string.need_execute_task, needRunTaskCnt)
        }
        mNotification = createNotification(text.toString())
        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, mNotification!!)

    }

    /**
     * 判断当前位置是否在任务规定范围内
     */
    private fun isRange(task: TaskRecord, curLatlng: LatLng): Boolean {
        val center = LatLng(task.Lat.toDouble(), task.Lng.toDouble())
        return SpatialRelationUtil.isCircleContainsPoint(center, task.radius, curLatlng)
    }

    /**
     * 检查是否有任务需要执行
     */
    private fun checkTask(tasks: List<TaskRecord>, curLatlng: LatLng): Int {
        val checkInTasks = mutableListOf<TaskRecord>()
        val checkOutTasks = mutableListOf<TaskRecord>()
        for (task in tasks) {
            val taskStatus = TaskUtils.getTaskStatus(task)
            if (taskStatus == TaskRecord.STATUS_READY) {
                val isRange = isRange(task, curLatlng)
                if (isRange && task.type == TaskRecord.TYPE_CHECK_IN) {
                    //进入打卡范围
                    checkInTasks.add(task)
                } else if (!isRange && task.type == TaskRecord.TYPE_CHECK_OUT) {
                    //离开打卡范围
                    checkOutTasks.add(task)
                }

            }
        }
        return checkInTasks.size + checkOutTasks.size
    }
}