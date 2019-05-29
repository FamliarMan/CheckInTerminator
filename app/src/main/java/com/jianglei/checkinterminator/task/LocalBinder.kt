package com.jianglei.checkinterminator.task

import android.os.Binder

class LocalBinder(val scheduleServiceModel: ScheduleServiceModel  ) : Binder() {
    fun getRemoteService(): ScheduleServiceModel {
        return scheduleServiceModel
    }
}