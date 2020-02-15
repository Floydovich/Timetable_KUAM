package com.example.timetable_kuam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetable_kuam.adapters.ClassesAdapter
import com.example.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.day_fragment.*

class DayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.day_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ClassesAdapter(
            arguments?.getInt(ARG_DAY),
            arguments?.getString(ARG_JSON))
    }

    companion object {
        fun newInstance(position: Int, jsonTable: String): DayFragment {
            val fragment = DayFragment()
            val arguments = Bundle()

            arguments.putInt(ARG_DAY, position)
            arguments.putString(ARG_JSON, jsonTable)
            fragment.arguments = arguments

            return fragment
        }
    }
}