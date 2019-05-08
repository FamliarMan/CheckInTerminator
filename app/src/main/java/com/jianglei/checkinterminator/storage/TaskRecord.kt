package com.jianglei.girlshow.storage

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

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
    var enabled: Int,

    /**
     * 任务开始具体时间，如 16:00
     */
    @ColumnInfo(name = "startTime")
    var startTime: String,

    /**
     * 星期几生效，用"，"隔开，如星期一，星期二
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
     * 上一次完成任务时间
     */
    var lastDoneTime: Long


) : Parcelable {
    constructor(source: Parcel) : this(
        source.readInt(),
        source.readString(),
        source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readLong()
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
        writeLong(lastDoneTime)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TaskRecord> = object : Parcelable.Creator<TaskRecord> {
            override fun createFromParcel(source: Parcel): TaskRecord = TaskRecord(source)
            override fun newArray(size: Int): Array<TaskRecord?> = arrayOfNulls(size)
        }
    }
}
