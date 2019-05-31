package com.jianglei.checkinterminator

import com.jianglei.checkinterminator.util.TaskUtils
import com.jianglei.girlshow.storage.TaskRecord
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 *@author longyi created on 19-5-30
 */
class TaskUtilsTest {

    @Test
    fun getNextRemindTimeTest() {
        val taskRecord = TaskRecord(
            0, "上班", 1,
            "15:00", "1",
            "", "", "", System.currentTimeMillis(), 0, 50
        )

        val res = TaskUtils.getNextRemindTime(taskRecord)
        val sdf1 = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        System.out.println(sdf1.format(Date(System.currentTimeMillis() + res)))

    }
}