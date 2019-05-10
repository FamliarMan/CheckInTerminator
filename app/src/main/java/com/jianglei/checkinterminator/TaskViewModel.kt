package com.jianglei.checkinterminator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.jianglei.girlshow.storage.DataStorage
import com.jianglei.girlshow.storage.TaskRecord

/**
 *@author longyi created on 19-5-10
 */
class TaskViewModel : ViewModel() {
    fun getTasks(): LiveData<List<TaskRecord>> {
        return DataStorage.db.taskDao().getAllTasks()
    }

    fun updateTask(taskRecord: TaskRecord){
        DataStorage.db.taskDao().updateRule(taskRecord)
    }
}