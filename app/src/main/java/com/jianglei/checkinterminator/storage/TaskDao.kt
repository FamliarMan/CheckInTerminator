package com.jianglei.girlshow.storage

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 *@author longyi created on 19-3-28
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM TaskRecord")
    fun getAllTasks(): LiveData<List<TaskRecord>>

    @Update
    fun updateRule(task:TaskRecord)

    @Insert
    fun addRule(task:TaskRecord)

    @Delete
    fun deleteRule(task:TaskRecord)
}