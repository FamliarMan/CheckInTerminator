package com.jianglei.checkinterminator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import com.jianglei.checkinterminator.location.LocationSelectActivity
import com.jianglei.checkinterminator.util.DateUtil
import com.jianglei.checkinterminator.util.DialogUtils
import com.jianglei.checkinterminator.widget.DaySelectDialog
import com.jianglei.girlshow.storage.DataStorage
import com.jianglei.girlshow.storage.TaskRecord
import kotlinx.android.synthetic.main.activity_task_edit.*

class TaskEditActivity : BaseActivity() {
    private var taskRecord: TaskRecord? = null
    private var isAdd = true
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit)
        taskRecord = intent.getParcelableExtra<TaskRecord>("task")
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
        if (taskRecord == null) {
            isAdd = true
            taskRecord = TaskRecord(
                0, "",
                1, "", "", "", "", "",
                1557469433000,
                TaskRecord.TYPE_CHECK_IN,
                50
            )
        } else {
            isAdd = false
        }
        fillUI()
        btnDelete.visibility = if (isAdd) {
            View.GONE
        } else {
            View.VISIBLE
        }
        tvTime.setOnClickListener {
            selectTime()
        }
        tvWeek.setOnClickListener {
            selectDay()
        }
        tvAddr.setOnClickListener {
            val intent = Intent(this@TaskEditActivity, LocationSelectActivity::class.java)
            startActivityForResult(intent, 100)
        }
        btnDelete.setOnClickListener {
            DialogUtils.showClickDialog(this, getString(R.string.confirm_delete),
                getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
                    taskViewModel.deleteTask(taskRecord!!)
                    finish()
                })
        }
        spType.adapter = object : ArrayAdapter<String>(
            this, R.layout.listitem_sp_type,
            //这里位置和打卡类型强相关，不能随意改动
            listOf("上班", "下班")
        ) {
        }
        spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                taskRecord!!.type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                taskRecord!!.type = TaskRecord.TYPE_CHECK_IN
            }
        }


    }

    private fun fillUI() {
        etName.setText(taskRecord!!.name)
        spType.setSelection(taskRecord!!.type)
        tvTime.text = taskRecord!!.startTime
        tvWeek.text = if (taskRecord!!.week.isBlank()) {
            ""
        } else {
            DateUtil.getDayStr(taskRecord!!.week)
        }
        tvAddr.text = taskRecord!!.addr
        etRadius.setText(taskRecord!!.radius.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            taskRecord!!.addr = data.getStringExtra("addr")
            taskRecord!!.Lat = data.getDoubleExtra("lat", 0.0).toString()
            taskRecord!!.Lng = data.getDoubleExtra("lng", 0.0).toString()
            tvAddr.text = taskRecord!!.addr

        }
    }

    private fun selectTime() {
        val dialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            @SuppressLint("SetTextI18n")
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if(minute<10){

                    tvTime.setText("$hourOfDay:0$minute")
                }else{
                    tvTime.setText("$hourOfDay:$minute")
                }
            }
        }, 0, 0, true)
        dialog.show()
    }

    private fun selectDay() {
        val dialog = DaySelectDialog()
        dialog.onDaySelectListener = object : DaySelectDialog.OnDaySelectListener {

            override fun onSelect(res: List<Int>) {
                tvWeek.text = DateUtil.getDayStr(res)
                taskRecord?.week = DateUtil.getDayIntStr(res)
            }
        }
        dialog.show(supportFragmentManager, "day")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return super.onOptionsItemSelected(item)
        }
        if (item.itemId == R.id.menuConfirm) {
            save()

        }
        return super.onOptionsItemSelected(item)
    }

    fun save() {
        if (!isValid()) {
            return
        }
        taskRecord!!.name = etName.text.toString()
        taskRecord!!.startTime = tvTime.text.toString()
        taskRecord!!.addr = tvAddr.text.toString()
        taskRecord!!.radius = etRadius.text.toString().toInt()
        if (isAdd) {
            taskViewModel.addTask(taskRecord!!)
        } else {
            taskViewModel.updateTask(taskRecord!!)
        }
        finish()

    }

    private fun isValid(): Boolean {
        if (etName.text.isNullOrBlank()
            || tvTime.text.isNullOrBlank()
            || tvWeek.text.isNullOrBlank()
            || tvAddr.text.isNullOrBlank()
            || etRadius.text.isNullOrBlank()
        ) {
            DialogUtils.showTipDialog(this, getString(R.string.content_empty))
            return false
        }
        return true
    }
}
