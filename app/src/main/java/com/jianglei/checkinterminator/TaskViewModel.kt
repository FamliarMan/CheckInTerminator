package com.jianglei.checkinterminator

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.jianglei.girlshow.storage.DataStorage
import com.jianglei.girlshow.storage.TaskRecord
import org.jetbrains.anko.doAsync

/**
 *@author longyi created on 19-5-10
 */
class TaskViewModel : ViewModel() {
    fun getTasks(): LiveData<List<TaskRecord>> {
        return DataStorage.db.taskDao().getAllTasks()
    }

    fun updateTask(taskRecord: TaskRecord){
        doAsync {
            DataStorage.db.taskDao().updateRule(taskRecord)
        }
    }
    fun addTask (taskRecord: TaskRecord){
        doAsync {
            DataStorage.db.taskDao().addRule(taskRecord)
        }
    }

    fun deleteTask(taskRecord: TaskRecord){
        doAsync {
            DataStorage.db.taskDao().deleteRule(taskRecord)
        }
    }
}