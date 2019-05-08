package com.jianglei.checkinterminator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonAdapter
import com.jianglei.girlshow.storage.TaskRecord

class TaskListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
    }

    class TaskAdapter(context:Context,tasks:List<TaskRecord>):
        CommonAdapter<TaskRecord>(context,R.layout.,tasks) {
        override fun onUpdate(helper: BaseAdapterHelper?, item: TaskRecord?, position: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
