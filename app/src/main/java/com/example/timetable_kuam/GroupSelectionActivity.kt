package com.example.timetable_kuam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.timetable_kuam.utils.FILE_PATH

import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var specs: Array<String>
    private var groups = arrayOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        specs = assets.list("json_files") as Array<String>

        spinnerGroups.adapter = setSpinnerAdapter(groups)
        spinnerSpecs.adapter = setSpinnerAdapter(specs)

        spinnerSpecs.onItemSelectedListener = this

        // Нажатие по кнопке передаёт путь до JSON файла и переходит к расписанию
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(FILE_PATH,
                "json_files/${spinnerSpecs.selectedItem}/${spinnerGroups.selectedItem}.json")
            startActivity(intent)
        }
    }

    // Сразу устанавливает адаптер для спиннера и два лэйаута: для спиннера и его пунктов
    private fun setSpinnerAdapter(itemsArray: Array<String>) = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            itemsArray).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("LISTENER NOT SELECTED", "Nothing is selected.")
    }

    // При выборе специальности берёт список групп из папки специальности
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        groups = assets.list("json_files/${specs[position]}") as Array<String>

        // Устанавливает список групп с убранным расширением в конце
        spinnerGroups.adapter = setSpinnerAdapter(groups.map { fileName ->
            fileName.dropLast(5) } .toTypedArray())
    }
}
