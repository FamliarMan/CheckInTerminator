package com.jianglei.checkinterminator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.checkinterminator.task.AlarmUtils
import com.jianglei.checkinterminator.task.LocalBinder
import com.jianglei.checkinterminator.task.ScheduleService
import com.jianglei.checkinterminator.task.ScheduleServiceModel
import com.jianglei.checkinterminator.util.TaskUtils
import com.jianglei.girlshow.storage.TaskRecord
import kotlinx.android.synthetic.main.activity_task_list.*
import org.jetbrains.anko.alarmManager
import java.util.*

class TaskListActivity : BaseActivity() {
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var model: TaskViewModel
    private var scheduleServiceModel: ScheduleServiceModel? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            scheduleServiceModel = null
            Log.d("longyi", "ScheduleService disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            scheduleServiceModel = (service as LocalBinder).getRemoteService()
            Log.d("longyi", "ScheduleService connected")

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        rvContent.layoutManager = LinearLayoutManager(this)
        model = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        model.getTasks().observe(this, object : Observer<List<TaskRecord>> {
            override fun onChanged(t: List<TaskRecord>?) {

                if (rvContent.adapter == null) {
                    mTaskAdapter = TaskAdapter(t)
                    mTaskAdapter.setOnItemClickListener(object : CommonRecyclerAdapter.OnItemClickListener {
                        override fun onItemClick(viewHolder: RecyclerView.ViewHolder?, view: View?, position: Int) {
                            val intent = Intent(this@TaskListActivity, TaskEditActivity::class.java)
                            intent.putExtra("task", mTaskAdapter.getItem(position))
                            startActivity(intent)
                        }
                    })
                    rvContent.adapter = mTaskAdapter
                } else {
                    if (t == null) {
                        mTaskAdapter.clear()
                    } else {
                        mTaskAdapter.replaceAll(t, true)
                    }
                    //数据有更新，要重新设置定时提醒
                    AlarmUtils.registryAlarm(application,t)
                }
            }
        })
        val intent = Intent(this, ScheduleService::class.java)
        bindService(intent, serviceConnection, 0)
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
            val taskStatus = TaskUtils.getTaskStatus(item)
            val tvStatus = helper.getView<TextView>(R.id.tvStatus)
            if (taskStatus == TaskRecord.STATUS_SKIP) {
                tvStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_grey_button)
                tvStatus.text = getString(R.string.skip)
                btnStatus.visibility = View.GONE
            } else if (taskStatus == TaskRecord.STATUS_DONE) {
                tvStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_grey_button)
                tvStatus.text = getString(R.string.already_done)
                btnStatus.visibility = View.GONE
            } else if (taskStatus == TaskRecord.STATUS_READY) {
                tvStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_green_rec)
                tvStatus.text = getString(R.string.ready_remind)
                btnStatus.visibility = View.VISIBLE
                btnStatus.text = getString(R.string.finish_ahead_of_schedule)
            } else {
                tvStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_red_button)
                tvStatus.text = getString(R.string.reminding)
                btnStatus.visibility = View.VISIBLE
                btnStatus.text = getString(R.string.finish_task)
            }
            btnStatus.setOnClickListener {
                if (taskStatus == TaskRecord.STATUS_READY || taskStatus == TaskRecord.STATUS_ACTIVIE) {
                    item.lastDoneTime = System.currentTimeMillis()
                    model.updateTask(item)
                    scheduleServiceModel?.finishTask(item)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return super.onOptionsItemSelected(item)
        }
        if (item.itemId == R.id.add_task) {
            val intent = Intent(this@TaskListActivity, TaskEditActivity::class.java)
            startActivity(intent)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
