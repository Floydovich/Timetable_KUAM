package com.example.timetable_kuam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonFile = assets.open("timetable.json")
        // TODO: Добавить код для чтения InputStream в строку
    }
}
