package com.it_club.timetable_kuam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.it_club.timetable_kuam.db.FirestoreService
import com.it_club.timetable_kuam.utils.GROUP_NAME
import com.it_club.timetable_kuam.utils.CHAIR_NAME
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class GroupSelectionActivity : AppCompatActivity() {

    private val chairs = listOf(
        "Дизайн и КДР",
        "Ин.яз. и Переводческое дело",
//        "ИС и Информатика",  TODO: Change in DB and uncomment
        "Информационные системы",
        "МО, История и СР",
        "ОПДЭТ и ПО",
        "Соц.-пед. дисциплины",
        "Туризм, НВП и ФкС",
        "Учет и Управление",
        "Финансы",
        "Экология и БЖиЗОС",
        "Юриспруденция"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        // TODO: How the firestore connection without the Internet will work?
        Log.d(TAG, "Firestore connected: ${FirestoreService.db.firestoreSettings.toString()}")

        initSpinners()
        initButton()
    }

    private fun initSpinners() {
        spinnerChair.item = chairs

        spinnerGroup.setOnEmptySpinnerClickListener {
            if (spinnerGroup.isClickable)
                spinnerGroup.errorText = "Выберите кафедру"
            else
                spinnerGroup.errorText = ""
        }

        spinnerChair.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (spinnerGroup.errorText == "Выберите кафедру")
                    spinnerGroup.errorText = ""

                // Prevent selecting a group before they are loaded from the DB
                spinnerGroup.apply {
                    isClickable = false
                    floatingLabelText = "Загружается список групп"
                    showFloatingLabel()
                }

                // TODO: Refactor this to object method
                FirestoreService.db.collection(spinnerChair.selectedItem.toString())
                    .get()
                    .addOnSuccessListener { result ->
                        spinnerGroup.apply {
                            item = result.documents.map { it.id }
                            hideFloatingLabel()
                            isClickable = true
                        }
                    }
                    .addOnFailureListener { exception ->
                        // It works without connection. DB is saved in cache?
                        Log.d(TAG, "Error getting documents", exception)
                    }
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
                spinnerChair.errorText = ""
                spinnerGroup.apply {
                    hideFloatingLabel()  // selecting a group floats text again
                    errorText = ""
                }
            }
        }
    }

    private fun initButton() {
        button.setOnClickListener {
            val chair = spinnerChair.selectedItem
            val group = spinnerGroup.selectedItem

            when {
                chair == null -> {
                    spinnerChair.errorText = "Выберите кафедру"
                }
                group == null -> {
                    spinnerGroup.errorText = "Выберите группу"
                }
                else -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.apply {
                        putExtra(CHAIR_NAME, chair.toString())
                        putExtra(GROUP_NAME, group.toString())
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    companion object {
        const val TAG = "GroupSelectionActivity"
    }
}
