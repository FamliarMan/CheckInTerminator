package com.jianglei.checkinterminator.util

import com.jianglei.girlshow.storage.TaskRecord
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
            if (!isNeedRunToday(taskRecord)) {
                return TaskRecord.STATUS_SKIP
            }
            if (nowCalendar.get(Calendar.DAY_OF_MONTH) - finishCalendar.get(Calendar.DAY_OF_MONTH) >= 1) {
                //离上次任务完成已经过了至少一天
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

    }
}