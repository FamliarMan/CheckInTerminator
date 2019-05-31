package com.jianglei.checkinterminator.widget

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import com.classic.adapter.BaseAdapterHelper
import com.classic.adapter.CommonRecyclerAdapter
import com.jianglei.checkinterminator.R
import com.jianglei.checkinterminator.Selectable

/**
 *@author longyi created on 19-5-13
 */
class DaySelectDialog : DialogFragment() {

    companion object {
        fun newInstance(selectedDays: String): DaySelectDialog {
            val bundle = Bundle()
            bundle.putString("selectedDays", selectedDays)
            val dialog = DaySelectDialog()
            dialog.arguments = bundle
            return dialog
        }
    }

    private val days = listOf(
        Selectable("星期天"),
        Selectable("星期一"),
        Selectable("星期二"),
        Selectable("星期三"),
        Selectable("星期四"),
        Selectable("星期五"),
        Selectable("星期六")
    )
    //类似于"1,3,7"
    private var selectedDayStr: String? = null

    var onDaySelectListener: OnDaySelectListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_day_select, container, false)
        val rvContent = view.findViewById<RecyclerView>(R.id.rvContent)
        if (arguments != null) {
            selectedDayStr = arguments!!.getString("selectedDays")
        }
        selectedDayStr?.split(",")?.forEach {
            days[it.toInt()-1].isSelect = true
        }
        rvContent.layoutManager = LinearLayoutManager(context)
        rvContent.adapter =
            object : CommonRecyclerAdapter<Selectable<String>>(context!!, R.layout.listitem_day_select, days) {
                override fun onUpdate(helper: BaseAdapterHelper?, item: Selectable<String>?, position: Int) {
                    if (helper == null || item == null) {
                        return
                    }
                    helper.setText(R.id.tvDay, item.data)
                    helper.setChecked(R.id.cbChoose, item.isSelect)
                    helper.getView<CheckBox>(R.id.cbChoose)
                        .setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                                item.isSelect = isChecked
                            }
                        })
                }

            }
        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            onDaySelectListener?.onSelect(getSelectDay())
            dismiss()
        }
        return view
    }

    fun getSelectDay(): List<Int> {
        val res = mutableListOf<Int>()
        for (index in 0 until days.size) {

            val day = days[index]
            if (day.isSelect) {
                res.add(index + 1)
            }

        }
        return res
    }

    interface OnDaySelectListener {
        fun onSelect(res: List<Int>)
    }
}