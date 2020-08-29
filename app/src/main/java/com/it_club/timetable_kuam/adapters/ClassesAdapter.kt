package com.it_club.timetable_kuam.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it_club.timetable_kuam.R
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.getEndTime
import com.it_club.timetable_kuam.utils.getStartTime
import com.it_club.timetable_kuam.utils.inflate
import kotlinx.android.synthetic.main.item_class.view.startTime
import kotlinx.android.synthetic.main.item_class.view.endTime
import kotlinx.android.synthetic.main.item_class.view.name
import kotlinx.android.synthetic.main.item_class.view.place
import kotlinx.android.synthetic.main.item_class.view.prof
import kotlinx.android.synthetic.main.item_class.view.number

class ClassesAdapter(private val classes: Array<ClassItem>)
    : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    override fun getItemCount() = classes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_class))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*
        Выбираем класс из classes по его позиции. Вызываем функцию bind из класса BaseViewHolder
        передавая ему выбранный класс.
         */
        val classItem = classes[position]

        holder.bind(classItem)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private fun setTimeAndNumber(id: Int) {
            /*
            Устанавливает поля времен и номер пары, используя методы класса TimeUtils
             */
            itemView.startTime.text = getStartTime(id)
            itemView.endTime.text = getEndTime(id)
            itemView.number.text = (id + 1).toString()
        }

        private fun setTexts(name: String, prof: String, place: String) {
            /*
            Устанавливает текстовые поля для названия, препода и места для обычного layout или для
            левой половины для двойного layout.
             */
            itemView.name.text = name
            itemView.prof.text = prof
            itemView.place.text = place
        }

        fun bind(item: ClassItem) {
            /*
            Перезаписывает функцию bind в родительском классе BaseViewHolder. Заполяем функцию
            нужным кодом: вызов функций для заполнения полей layout и смена фона для блока времени
            в пустых парах.
             */
            setTimeAndNumber(item.class_id)
            setTexts(item.name, item.prof, item.place)
        }
    }
}