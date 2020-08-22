package com.it_club.timetable_kuam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.it_club.timetable_kuam.utils.GROUP_NAME
import com.it_club.timetable_kuam.utils.SPEC_NAME
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity() {

    private lateinit var chairs: List<String>

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

        chairs = listOf(
            "Дизайн и КДР",
            "Ин.яз. и Переводческое дело",
            "ИС и Информатика",
            "МО, История и СР",
            "ОПДЭТ И ПО",
            "Соц.-пед. дисциплины",
            "Туризм, НВП и ФкС",
            "Учет и Управление",
            "Финансы",
            "Экология и БЖиЗОС",
            "Юриспруденция"
        )

        initSpinners()

        initButton()
    }

    private fun initSpinners() {
        /*
        Устанавливает содержимое списка выбора специальностей и задаёт им датчики выбора
        onItemSelectedListener. Для спиннера групп также задаётся датчик клика setOnEmptySpinnerClickListener.
         */
        spinnerChair.item = chairs

        spinnerGroup.setOnEmptySpinnerClickListener {
            spinnerGroup.errorText = "Сначала выберите специальность"
        }

        spinnerChair.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

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
                spinnerChair.errorText = " "

                if (spinnerGroup.errorText == "Сначала выберите специальность")
                    spinnerGroup.errorText = " "

                /*
                Метод list возвращает массив из названий файлов в папке assets/chairs/<специальность>.
                При нажатии передаётся порядковый номер специальности в спиннере, что позволяет получить
                название из массива всех специальностей и добавить в путь.
                 */
                spinnerGroup.item = assets.list("chairs/${chairs[position]}")!!
                    .map { name -> name.dropLast(5) }  // убирает .json из названий групп
            }
        }

        spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                spinnerChair.errorText = " "
                spinnerGroup.errorText = " "
            }
        }
    }

    private fun initButton() {
        /*
        Кнопке задаётся датчик клика через метод setOnClickListener, который если кнопка была
        нажата, проверяет выбраны ли специальность и группа и запускает переход в расписание.
        Если они не выбраны, появляется сообщение об ошибке.
        */
        button.setOnClickListener {
            val spec = spinnerChair.selectedItem
            val group = spinnerGroup.selectedItem

            if (spec != null && group != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SPEC_NAME, spec.toString())
                intent.putExtra(GROUP_NAME, group.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                if (spec == null) spinnerChair.errorText = "Вы не выбрали специальность"
                if (group == null) spinnerGroup.errorText = "Вы не выбрали группу"
            }
        }
    }
}
