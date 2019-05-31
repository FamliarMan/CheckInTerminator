package com.jianglei.checkinterminator.util

import com.jianglei.girlshow.storage.TaskRecord
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 *@author longyi created on 19-5-14
 */
class TaskUtils {
    companion object {
        /**
         * 获取当前任务状态
         * 返回0
         */
        fun getTaskStatus(taskRecord: TaskRecord): Int {
            val finishDate = Date(taskRecord.lastDoneTime)
            val now = Date()
            val finishCalendar = Calendar.getInstance()
            finishCalendar.time = finishDate
            val nowCalendar = Calendar.getInstance()
            nowCalendar.time = now
            val remindTime = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(taskRecord.startTime)
            val remindCalendar = Calendar.getInstance()
            remindCalendar.time = remindTime
            remindCalendar.set(Calendar.YEAR, nowCalendar[Calendar.YEAR])
            remindCalendar.set(Calendar.MONTH, nowCalendar[Calendar.MONTH])
            remindCalendar.set(Calendar.DAY_OF_YEAR, nowCalendar[Calendar.DAY_OF_YEAR])
            //为了保险起见，提前10s提醒
            remindCalendar.add(Calendar.SECOND,-10)
            SimpleDateFormat.getDateInstance()
            if (!isNeedRunToday(taskRecord)) {
                return TaskRecord.STATUS_SKIP
            }
            if (nowCalendar.get(Calendar.DAY_OF_MONTH) - finishCalendar.get(Calendar.DAY_OF_MONTH) >= 1) {
                //离上次任务完成已经过了至少一天,所以今天需要提醒任务
                if (remindCalendar.time.before(now)) {
                    //已经超过了提醒时间
                    return TaskRecord.STATUS_ACTIVIE
                }
                return TaskRecord.STATUS_READY
            } else {
                return TaskRecord.STATUS_DONE
            }
        }

        /**
         * 判断任务今天是否需要执行
         */
        fun isNeedRunToday(taskRecord: TaskRecord): Boolean {

            val c = Calendar.getInstance()
            val week = c.get(Calendar.DAY_OF_WEEK)
            val validDays: List<Int> = taskRecord.week.split(",")
                .map {
                    Integer.valueOf(it)
                }

            return validDays.contains(week)
        }

        /**
         * 获取某个任务的下一次提醒时间距现在的毫秒数
         */
        fun getNextRemindTime(taskRecord: TaskRecord): Long {
            val lastRemindCalendar = Calendar.getInstance()
            lastRemindCalendar.time = Date()
            val nowCalendar = Calendar.getInstance()
            nowCalendar.time = Date()
            val remindDays = taskRecord.week
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val days = remindDays.split(",")
                .map {
                    it.toInt()
                }
            //设置小时和分钟
            val startTime = sdf.parse(taskRecord.startTime)
            val startRemind = Calendar.getInstance()
            startRemind.time = startTime
            lastRemindCalendar.set(Calendar.HOUR, startRemind.get(Calendar.HOUR))
            lastRemindCalendar.set(Calendar.MINUTE, startRemind.get(Calendar.MINUTE))
            for (day in days) {
                lastRemindCalendar.set(Calendar.DAY_OF_WEEK, day)
                if (lastRemindCalendar.after(nowCalendar)) {
                    //由于星期是按顺序排列的，我们找到从现在往后的第一个时间即可
                    return lastRemindCalendar.time.time - System.currentTimeMillis()
                }
            }

            //说明本星期从今天到周六都没有新的提醒任务了，那最近的提醒任务就是下一个星期的第一个提醒任务时间
            lastRemindCalendar.set(Calendar.DAY_OF_WEEK, days[0])
            //星期+1
            lastRemindCalendar.add(Calendar.WEEK_OF_MONTH, 1)
            return lastRemindCalendar.time.time - System.currentTimeMillis()
        }

        /**
         * 获取一堆任务中离现在最近的一个提醒时间
         */
        fun getNextRemindTime(tastRecords: List<TaskRecord>): Long {
            var res: Long = Integer.MAX_VALUE.toLong()
            tastRecords.forEach {
                val remindTime = getNextRemindTime(it)
                if (remindTime < res) {
                    res = remindTime
                }
            }
            return res
        }
    }
}