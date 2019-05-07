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
    var startTime:String,

    /**
     * 星期几生效，用"，"隔开，如星期一，星期二
     */
    @ColumnInfo(name = "week")
    var week:String,


    /**
     * 位置中心的经度
     */
    @ColumnInfo(name = "Lng")
    var Lng:String,

    /**
     * 位置中心的维度
     */
    @ColumnInfo(name="Lat")
    var Lat:String


):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<TaskRecord> {
        override fun createFromParcel(parcel: Parcel): TaskRecord {
            return TaskRecord(parcel)
        }

        override fun newArray(size: Int): Array<TaskRecord?> {
            return arrayOfNulls(size)
        }
    }

}
