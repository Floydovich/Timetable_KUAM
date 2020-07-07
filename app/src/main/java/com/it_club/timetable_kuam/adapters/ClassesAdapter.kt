package com.it_club.timetable_kuam.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it_club.timetable_kuam.R
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.TimeUtils
import com.it_club.timetable_kuam.utils.inflate
import kotlinx.android.synthetic.main.item_class.view.timebox
import kotlinx.android.synthetic.main.item_class.view.startTime
import kotlinx.android.synthetic.main.item_class.view.endTime
import kotlinx.android.synthetic.main.item_class.view.name
import kotlinx.android.synthetic.main.item_class.view.place
import kotlinx.android.synthetic.main.item_class.view.prof
import kotlinx.android.synthetic.main.item_class.view.number
import kotlinx.android.synthetic.main.item_class_blink.view.*

class ClassesAdapter(private val classes: Array<ClassItem>)
    : RecyclerView.Adapter<ClassesAdapter.BaseViewHolder<ClassItem>>() {

    // Возвращает количество предметов для заполнения адаптера
    override fun getItemCount() = 1

//     Возвращает количество названий в конкретной паре, чтобы определить какой layout показывать
//    override fun getItemViewType(position: Int) = classes[position].name.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ClassItem> {
        /*
        Зная количество названий в паре, выбираем какой класс и какой layout
        передать адаптеру для заполнения.
         */
        // TODO: Разобратьс с мигающими парами
//        return when(viewType) {
//            1 -> NormalViewHolder(parent.inflate(R.layout.item_class))
//            else -> BlinkViewHolder(parent.inflate(R.layout.item_class_blink))
//        }
        return NormalViewHolder(parent.inflate(R.layout.item_class))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ClassItem>, position: Int) {
        /*
        Выбираем класс из classes по его позиции. Вызываем функцию bind из класса BaseViewHolder
        передавая ему выбранный класс.
         */
        val classItem = classes[position]

        holder.bind(classItem)
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*
        Абстрактный класс используется для того чтобы можно было использовать один класс для
        заполнения разных layout. Другие классы наследуют от этого класса и могу передаваться
        функции onCreateViewHolder. Функция bind перезаписывается в классах-потомках.
         */

        abstract fun bind(item: T)

        fun setTimeAndNumber(id: Int) {
            /*
            Устанавливает поля времен и номер пары, используя методы класса TimeUtils
             */
            itemView.startTime.text = TimeUtils.getStartTime(id)
            itemView.endTime.text = TimeUtils.getEndTime(id)
            itemView.number.text = (id + 1).toString()
        }

        open fun setTexts(name: String, prof: String, place: String) {
            /*
            Устанавливает текстовые поля для названия, препода и места для обычного layout или для
            левой половины для двойного layout.
             */
            itemView.name.text = name
            itemView.prof.text = prof
            itemView.place.text = place
        }

        fun changeTimeBg() {
            // Находит более светлый фон для времени и ставит его к аттрибуту timebox
            itemView.timebox.setBackgroundResource(R.drawable.time_rounded_less_visible)
        }
    }

    inner class NormalViewHolder(itemView: View) : BaseViewHolder<ClassItem>(itemView) {
        /*
        Этот класс используется для обычных пар. Он расширяет класс BaseViewHolder что позволяет
        использовать его при создании элемента адаптера.
         */

        override fun bind(item: ClassItem) {
            /*
            Перезаписывает функцию bind в родительском классе BaseViewHolder. Заполяем функцию
            нужным кодом: вызов функций для заполнения полей layout и смена фона для блока времени
            в пустых парах.
             */
            setTimeAndNumber(item.id)
            setTexts(item.name, item.prof, item.place)

//            if (item.name[0] == " ")
//                changeTimeBg()
        }
    }

    inner class BlinkViewHolder(itemView: View) : BaseViewHolder<ClassItem>(itemView) {
        /*
        Второй класс, который наследует BaseViewHolder. Будет использоваться для item_class_blink.
        По умолчанию наслудуются функции родительского класса.
         */

//        override fun setTexts(name: String, prof: String, place: String) {
//            /*
//            Перезаписывается функция setTexts. Перед объявлением собственного кода в функции,
//            вызываем неперезаписанную родительскую фукнцию, передавая ей параметры через ключевое
//            слово super.
//             */
//            super.setTexts(name, prof, place)
//
//            itemView.name2.text = name
//            itemView.prof2.text = prof
//            itemView.place2.text = place
//        }

        override fun bind(item: ClassItem) {
            setTimeAndNumber(item.id)

            setTexts(item.name, item.prof, item.place)

//            if (item.name[0] == " " && item.name[1] == " ")
////                changeTimeBg()
        }
    }
}