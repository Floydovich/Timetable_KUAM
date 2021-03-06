package com.it_club.timetable_kuam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.it_club.timetable_kuam.adapters.ClassesAdapter
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.ARG_TIMETABLE
import kotlinx.android.synthetic.main.day_fragment.*

@Suppress("UNCHECKED_CAST")
class DayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.day_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val timetable = arguments?.getParcelableArray(ARG_TIMETABLE)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ClassesAdapter(timetable as Array<ClassItem>)
    }

    companion object {
        fun newInstance(timetable: List<ClassItem>): DayFragment {
            /*
            Pass the timetable in bundle, since if it is passed to a property, the app will crash
            on the display reverting.
             */
            val fragment = DayFragment()
            val arguments = Bundle()

            arguments.putParcelableArray(ARG_TIMETABLE, timetable.toTypedArray())
            fragment.arguments = arguments

            return fragment
        }
    }
}