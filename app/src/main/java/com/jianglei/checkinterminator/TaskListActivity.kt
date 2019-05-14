package com.jianglei.checkinterminator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.checkinterminator.util.TaskUtils
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
            val taskStatus = TaskUtils.getTaskStatus(item)
            if (taskStatus == TaskRecord.STATUS_SKIP) {
                btnStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_grey_button)
                btnStatus.text = getString(R.string.skip)
            } else if (taskStatus == TaskRecord.STATUS_DONE) {
                btnStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_grey_button)
                btnStatus.text = getString(R.string.already_done)
            } else {
                btnStatus.background = ContextCompat.getDrawable(this@TaskListActivity, R.drawable.shape_red_button)
                btnStatus.text = getString(R.string.finish_task)
            }
            btnStatus.setOnClickListener {
                if (taskStatus == TaskRecord.STATUS_READY) {
                    item.lastDoneTime = System.currentTimeMillis()
                    model.updateTask(item)
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
}
