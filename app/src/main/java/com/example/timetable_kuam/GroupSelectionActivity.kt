package com.example.timetable_kuam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var specs: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        specs = assets.list("json_files")!!

        val adapterSpecs = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            specs)

        adapterSpecs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpecs.adapter = adapterSpecs
        spinnerSpecs.onItemSelectedListener = this

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val groups = assets.list("json_files/${specs[position]}")

        val adapterGroups = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            groups?.map { fileName -> fileName.dropLast(5) } !!
        )

        adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGroups.adapter = adapterGroups
    }
}
