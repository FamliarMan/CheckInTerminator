package com.jianglei.checkinterminator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.activity_base)
    }

    override fun setContentView(layoutResID: Int) {
        setSupportActionBar(toolbar)
        LayoutInflater.from(this).inflate(layoutResID, mainLayout, true)
    }

    override fun setContentView(view: View?) {
        setSupportActionBar(toolbar)
        mainLayout.addView(view)
    }
}
