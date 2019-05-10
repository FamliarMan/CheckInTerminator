package com.jianglei.checkinterminator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.girlshow.storage.TaskRecord
import kotlinx.android.synthetic.main.activity_task_list.*
import java.util.*

class TaskListActivity : BaseActivity() {
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var model: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        rvContent.layoutManager = LinearLayoutManager(this)
        model = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        model.getTasks().observe(this, object : Observer<List<TaskRecord>> {
            override fun onChanged(t: List<TaskRecord>?) {

                if (rvContent.adapter == null) {
                    mTaskAdapter = TaskAdapter(t)
                    rvContent.adapter = mTaskAdapter
                } else {
                    mTaskAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    inner class TaskAdapter(tasks: List<TaskRecord>?) :
        CommonRecyclerAdapter<TaskRecord>(this@TaskListActivity, R.layout.listitem_task, tasks) {
        override fun onUpdate(helper: BaseAdapterHelper?, item: TaskRecord?, position: Int) {
            if (helper == null || item == null) {
                return
            }
            helper.setText(R.id.tvName, item.name)
            helper.setText(R.id.tvStartTime, item.startTime)
            val btnStatus = helper.getView<Button>(R.id.btnStatus)
            val isTaskDone = isTaskDone(item)
            if (isTaskDone) {
                btnStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_grey_button)
                btnStatus.text = getString(R.string.already_done)
            } else {
                btnStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_red_button)
                btnStatus.text = getString(R.string.finish_task)
            }
            btnStatus.setOnClickListener {
                if (!isTaskDone) {
                    model.updateTask(item)
                }
            }
        }

        /**
         * 判断任务是否完成
         */
        private fun isTaskDone(taskRecord: TaskRecord): Boolean {
            val finishDate = Date(taskRecord.lastDoneTime)
            val now = Date()
            val finishCalendar = Calendar.getInstance()
            finishCalendar.time = finishDate
            val nowCalendar = Calendar.getInstance()
            nowCalendar.time = now
            return if (nowCalendar.get(Calendar.DAY_OF_MONTH) - finishCalendar.get(Calendar.DAY_OF_MONTH) >= 1) {
                //离上次任务完成已经过了至少一天
                val week = nowCalendar.get(Calendar.DAY_OF_WEEK)
                val validDays: List<Int> = taskRecord.week.split(",")
                    .map {
                        Integer.valueOf(it)
                    }
                validDays.contains(week)
            } else {
                true
            }
        }

    }
}
