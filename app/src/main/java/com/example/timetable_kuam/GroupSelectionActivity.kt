package com.example.timetable_kuam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.timetable_kuam.utils.*

import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // Лист специальностей будет заполнен позже.
    private lateinit var specs: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        Функция вызывается при первом запуске приложения (создании активити).
         */
        super.onCreate(savedInstanceState)

        setActivityView()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("SPEC_LISTENER", "Nothing is selected.")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        /*
        Когда выбирается элемент в списке специальностей, наполнение списка групп меняется.
        assets.list находит все файлы в папке специальности, делает из них массив и передаёт его
        адаптеру списка групп.
         */
        val groups = assets.list("specs/${specs[position]}") as Array<String>

        spinnerGroups.adapter = setSpinnerAdapter(
            groups.map { fileName ->
                fileName.dropLast(5) }.toTypedArray()  // убирает расширение в конце файла
        )
    }

    private fun moveToMainWithSelectedGroup(filePath: String) {
        /*
        Переход в Мэйн Активити. Для этого создаётся экземпляр класс Интент с параметрами текущей
        активити и активити, куда надо перейти.мКладёт туда путь до JSON файла и стартует активити
         */
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(FILE_PATH, filePath)
        startActivity(intent)
        finish()
    }

    private fun setActivityView() {
        /*
        Настраивает вид для выбора группы, подгружая лэйаут и панель инструментов.
        Создаётся лист из из названий специальностей и наполняет выпадающий список.
        Далее устанавливает лиснер (уловитель) для переключения групп при выборе специальности.
         */
        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        specs = assets.list("specs") as Array<String>

        spinnerSpecs.adapter = setSpinnerAdapter(specs)

        spinnerSpecs.onItemSelectedListener = this
        setSpinnerListener()
        }

    private fun setSpinnerListener() {
        button.setOnClickListener {
            /*
            В конце вызывается функция перехода в другую активити.
            */
            val jsonFile = "specs/${spinnerSpecs.selectedItem}/${spinnerGroups.selectedItem}.json"

            moveToMainWithSelectedGroup(jsonFile) 
        } 
    }
    
    private fun setSpinnerAdapter(itemsArray: Array<String>): ArrayAdapter<String> {
        /*
        Создаёт адаптер для спиннера и передаёт ему контекст (текущая активити), лэйаут из
        библиотеки Андроид, и массив, из которого беруется значения для элементов выпадающего
        списка.
        Также, устанавливается лэйаут отдельного элемента.
         */
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            itemsArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
}
