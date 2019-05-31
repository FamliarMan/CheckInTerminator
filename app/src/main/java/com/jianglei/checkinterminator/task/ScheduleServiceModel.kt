package com.jianglei.checkinterminator.task

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.SpatialRelationUtil
import com.jianglei.checkinterminator.MyApplication
import com.jianglei.checkinterminator.R
import com.jianglei.checkinterminator.TaskViewModel
import com.jianglei.checkinterminator.util.TaskUtils
import com.jianglei.girlshow.storage.TaskRecord

/**
 *@author longyi created on 19-5-27
 */
class ScheduleServiceModel(val application: MyApplication) : TaskViewModel() {
    var mNotificationTitle: ObservableField<String> = ObservableField(application.getString(R.string.watching))
    var mCurLatLng: LatLng? = null
    /**
     * 提醒开关
     */
    var mRemindSwitch: ObservableBoolean = ObservableBoolean(false)
    var mServiceOn: ObservableBoolean = ObservableBoolean(true)

    /**
     * 开始提醒
     */
    public fun startRemind() {
        if (mRemindSwitch.get()) {
            return
        }
        mRemindSwitch.set(true)

    }

    /**
     * 停止提醒
     */
    public fun stopRemind() {
        if (!mRemindSwitch.get()) {
            return
        }
        mRemindSwitch.set(false)

    }

    /**
     * 完成一个任务
     */
    public fun finishTask(task: TaskRecord) {

        //完成任务后只需要重新检查所有任务就可以了
        checkTask()

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
    public fun checkTask() {
        if (mCurLatLng == null) {
            return
        }
        getTasks().observeForever {
            var cnt = 0

            val checkInTasks = mutableListOf<TaskRecord>()
            val checkOutTasks = mutableListOf<TaskRecord>()
            //保存达到提醒时间，但位置未达标的任务
            val waitPositionTasks = mutableListOf<TaskRecord>()
            if (it != null) {
                for (task in it) {
                    val taskStatus = TaskUtils.getTaskStatus(task)
                    if (taskStatus == TaskRecord.STATUS_ACTIVIE) {
                        val isRange = isRange(task, mCurLatLng!!)
                        if (isRange && task.type == TaskRecord.TYPE_CHECK_IN) {
                            //进入打卡范围
                            checkInTasks.add(task)
                        } else if (!isRange && task.type == TaskRecord.TYPE_CHECK_OUT) {
                            //离开打卡范围
                            checkOutTasks.add(task)
                        } else {
                            waitPositionTasks.add(task)
                        }

                    }
                }
                cnt = checkInTasks.size + checkOutTasks.size
            }

            if (cnt != 0) {
                val text = application.getString(R.string.need_execute_task, cnt)
                mNotificationTitle.set(text)
                startRemind()
            } else {
                val text = application.getString(R.string.watching)
                mNotificationTitle.set(text)
                stopRemind()
                if (waitPositionTasks.isEmpty()) {
                    //没有需要监控的任务
                    mServiceOn.set(false)
                    Log.d("longyi", "当前没有上班任务进入打卡范围或下班任务离开打开范围，停止服务")
                }
            }
        }
    }
}