package com.example.timetable_kuam

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val specs = listOf<String>("IS", "INF")

    private val groups = listOf<List<String>>(
        listOf("IS11", "IS42"),
        listOf("INF22", "INF31")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

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

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val adapterGroups = ArrayAdapter<String>(parent.context,
            android.R.layout.simple_spinner_item,
            groups[position])

        adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGroups.adapter = adapterGroups
    }
}
