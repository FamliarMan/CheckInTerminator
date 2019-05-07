package com.jianglei.girlshow.storage

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 *@author longyi created on 19-3-28
 */
class DataStorage {
    companion object {
        lateinit var db: AppDataBase


        fun init(context: Context) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
                "CheckInTerminator"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }
            }).build()
        }

    }
}

