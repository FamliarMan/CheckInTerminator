package com.jianglei.girlshow.storage

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 *@author longyi created on 19-3-28
 */

@Entity(tableName = "TaskRecord")
data class TaskRecord(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,
    /**
     * 该任务是否激活使用，0不使用，1使用
     */
    @ColumnInfo(name = "enabled")
    var enabled: Int = 1,

    /**
     * 任务开始具体时间，如 16:00
     */
    @ColumnInfo(name = "startTime")
    var startTime: String,

    /**
     * 星期几生效，用"，"隔开，1,2,3
     * 1——星期天
     * 2——星期一
     */
    @ColumnInfo(name = "week")
    var week: String,


    /**
     * 位置中心的经度
     */
    @ColumnInfo(name = "Lng")
    var Lng: String,

    /**
     * 位置中心的维度
     */
    @ColumnInfo(name = "Lat")
    var Lat: String,

    /**
     * 具体地址
     */
    @ColumnInfo(name = "addr")
    var addr: String,

    /**
     * 上一次完成任务时间
     */
    @ColumnInfo(name = "lastDoneTime")
    var lastDoneTime: Long = 1557469433000,

    /**
     * 任务类型，0——进入范围提醒， 1——离开范围提醒
     */
    @ColumnInfo(name = "type")
    var type: Int,
    /**
     * 有效半径
     */
    @ColumnInfo(name = "radius")
    var radius: Int


) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readLong(),
        source.readInt(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(id)
        writeString(name)
        writeInt(enabled)
        writeString(startTime)
        writeString(week)
        writeString(Lng)
        writeString(Lat)
        writeString(addr)
        writeLong(lastDoneTime)
        writeInt(type)
        writeInt(radius)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TaskRecord> = object : Parcelable.Creator<TaskRecord> {
            override fun createFromParcel(source: Parcel): TaskRecord = TaskRecord(source)
            override fun newArray(size: Int): Array<TaskRecord?> = arrayOfNulls(size)
        }

        const val TYPE_CHECK_IN = 0
        const val TYPE_CHECK_OUT = 1
        //任务跳过
        const val STATUS_SKIP = 0
        //任务已完成
        const val STATUS_DONE = 1
        //任务提醒时间还没到等待执行
        const val STATUS_READY = 2
        //任务激活中
        const val
                STATUS_ACTIVIE = 3
    }
}
