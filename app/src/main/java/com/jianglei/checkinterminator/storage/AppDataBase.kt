package com.jianglei.girlshow.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 *@author longyi created on 19-3-28
 */
@Database(entities = arrayOf(TaskRecord::class), version = 1,exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}