package com.example.sportjournal

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.sportjournal.databinding.ActivityMainBinding
import com.example.sportjournal.utilits.initFirebase
import com.example.sportjournal.utilits.initUser
import java.time.LocalDateTime

var CHOOSE_EXERCISE_MODE = false

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CHOOSE_EXERCISE_MODE = initInterface()
    }

    override fun onStart() {
        super.onStart()
        initFields()

    }

    private fun initFields() {
        initFirebase()
        initUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initInterface(): Boolean {
        val time = LocalDateTime.now().second
        return (time % 2 == 0)
    }
}