package com.example.timetable_kuam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity() {

    private lateinit var specs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        /*
        Функция вызывается при первом запуске приложения (создании активити).
        Настраивает вид для выбора группы, подгружая лэйаут и панель инструментов.
        Создаётся лист из из названий специальностей и наполняет выпадающий список.
        Далее устанавливает лиснер (уловитель) для переключения групп при выборе специальности.
         */
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        // TODO: Catch error if JSON file was deleted
        specs = assets.list("specs")!!.toList()
        initSpinners()

        initButton()
    }

    private fun initSpinners() {
        /*
        Устанавливает содержимое списка выбора специальностей и задаёт им датчики выбора
        onItemSelectedListener. Для спиннера групп также задаётся датчик клика setOnEmptySpinnerClickListener.
         */
        spinnerSpecs.item = specs

        spinnerGroups.setOnEmptySpinnerClickListener {
            spinnerGroups.errorText = "Сначала выберите специальность"
        }

        spinnerSpecs.onItemSelectedListener = SpecsListener(spinnerSpecs, spinnerGroups, specs, assets)

        spinnerGroups.onItemSelectedListener = GroupsListener(spinnerSpecs, spinnerGroups)
    }

    private fun initButton() {
        /*
        Кнопке задаётся датчик клика через метод setOnClickListener, который если кнопка была
        нажата, проверяет выбраны ли специальность и группа и запускает переход в расписание.
        Если они не выбраны, появляется сообщение об ошибке.
        */
        button.setOnClickListener {
            val spec = spinnerSpecs.selectedItem
            val group = spinnerGroups.selectedItem

            if (spec != null && group != null) {
                moveToTimetableWithSpecAndGroup(
                    spinnerSpecs.selectedItem.toString(),
                    spinnerGroups.selectedItem.toString()
                )
            } else {
                if (spec == null) spinnerSpecs.errorText = "Вы не выбрали специальность"
                if (group == null) spinnerGroups.errorText = "Вы не выбрали группу"
            }
        }
    }

    private fun moveToTimetableWithSpecAndGroup(spec: String, group: String) {
        /*
        Переход в расписание. В специальный класс Intent указывается контекст, то есть данная
        активити (откуда) и MainActivity как класс (куда). Метод putExtra помещается внутрь класса
        название специальности и группы. Intent передаётся функции startActivity для перехода.
         */
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SPEC_NAME", spec)
        intent.putExtra("GROUP_NAME", group)
        startActivity(intent)
        finish()  // если не стереть будет белый экран при возврате назад при первом входе
    }
}
