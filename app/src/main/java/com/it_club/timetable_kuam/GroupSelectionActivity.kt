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

        specs = assets.list("specs")!!.toList()

        initSpinners()

        initButton()
    }

    private fun initSpinners() {
        /*
        Устанавливает содержимое списка выбора специальностей и задаёт им датчики выбора
        onItemSelectedListener. Для спиннера групп также задаётся датчик клика setOnEmptySpinnerClickListener.
         */
        spinnerSpec.item = specs

        spinnerGroup.setOnEmptySpinnerClickListener {
            spinnerGroup.errorText = "Сначала выберите специальность"
        }

        spinnerSpec.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

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
                spinnerSpec.errorText = " "

                if (spinnerGroup.errorText == "Сначала выберите специальность")
                    spinnerGroup.errorText = " "

                /*
                Метод list возвращает массив из названий файлов в папке assets/specs/<специальность>.
                При нажатии передаётся порядковый номер специальности в спиннере, что позволяет получить
                название из массива всех специальностей и добавить в путь.
                 */
                spinnerGroup.item = assets.list("specs/${specs[position]}")!!
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
                spinnerSpec.errorText = " "
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
            val spec = spinnerSpec.selectedItem
            val group = spinnerGroup.selectedItem

            if (spec != null && group != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(SPEC_NAME, spec.toString())
                intent.putExtra(GROUP_NAME, group.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                if (spec == null) spinnerSpec.errorText = "Вы не выбрали специальность"
                if (group == null) spinnerGroup.errorText = "Вы не выбрали группу"
            }
        }
    }
}
