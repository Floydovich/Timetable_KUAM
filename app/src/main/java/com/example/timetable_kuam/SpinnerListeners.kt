package com.example.timetable_kuam

import android.content.res.AssetManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner

class SpecsListener(
    private val spinnerSpecs: SmartMaterialSpinner<*>,
    private val spinnerGroups: SmartMaterialSpinner<*>,
    private val specs: List<String>,
    private val assets: AssetManager
) : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Функция не должна вызываться
        Log.d("LISTENER_SPEC", "On nothing selected got called.")
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        /*
        Меняет текст ошибок у спиннеров, если был сделан выбор. Также выбор специальности,
        заполняет список групп.
         */
        spinnerSpecs.errorText = " "

        if (spinnerGroups.errorText == "Сначала выберите специальность")
            spinnerGroups.errorText = " "

        /*
        Метод list возвращает массив из названий файлов в папке assets/specs/<специальность>.
        При нажатии передаётся порядковый номер специальности в спиннере, что позволяет получить
        название из массива всех специальностей и добавить в путь.
         */
        spinnerGroups.item = assets.list("specs/${specs[position]}")!!
            .map { name -> name.dropLast(5) }  // убирает .json из названий групп
    }
}

class GroupsListener(
    private val spinnerSpecs: SmartMaterialSpinner<*>,
    private val spinnerGroups: SmartMaterialSpinner<*>
) : AdapterView.OnItemSelectedListener {

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("LISTENER_GROUP", "On nothing selected got called.")
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        spinnerSpecs.errorText = " "
        spinnerGroups.errorText = " "
    }
}

