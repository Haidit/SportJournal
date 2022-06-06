package com.example.sportjournal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportjournal.databinding.ActivityMainBinding
import com.example.sportjournal.utilits.initFirebase
import com.example.sportjournal.utilits.initUser

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onStart() {
        super.onStart()
        initFields()
    }

    private fun initFields() {
        initFirebase()
        initUser()
    }
}