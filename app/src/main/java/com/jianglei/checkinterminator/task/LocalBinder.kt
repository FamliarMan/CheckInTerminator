package com.jianglei.checkinterminator.task

import android.os.Binder

class LocalBinder(val service: ScheduleService) : Binder() {
    fun getRemoteService(): ScheduleService {
        return service
    }
}