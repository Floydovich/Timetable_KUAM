package com.it_club.timetable_kuam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.it_club.timetable_kuam.db.FirestoreService
import com.it_club.timetable_kuam.model.ClassItem
import com.it_club.timetable_kuam.utils.GROUP_NAME
import com.it_club.timetable_kuam.utils.CHAIR_NAME
import com.it_club.timetable_kuam.utils.IS_BLINKING
import com.it_club.timetable_kuam.utils.CLASS_ITEM_ARRAY
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class SelectionActivity : AppCompatActivity() {

    private val chairs = listOf(
        "Дизайн и КДР",
        "Ин.яз. и Переводческое дело",
//        "ИС и Информатика",
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
    // Groups will be loaded when user selects a chair from chairSpinner
    private lateinit var groups: List<String>
    private var selectedChair: String? = null
    private var selectedGroup: String? = null
    private var selectedGroupIsBlinking = false
    private lateinit var classItems: List<ClassItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        // How the firestore connection without the Internet will work?
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
                if (spinnerChair.errorText == "Выберите кафедру")
                    spinnerChair.errorText = ""

                // Prevent selecting a group before they are loaded from the DB
                spinnerGroup.apply {
                    isClickable = false
                    floatingLabelText = "Загружается список групп"
                    showFloatingLabel()
                }

                selectedChair = chairs[position]

                FirestoreService.chair(selectedChair!!)
                    .get()
                    .addOnSuccessListener { result ->
                        groups = result.documents.map { it.id }
                        spinnerGroup.apply {
                            item = groups
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
                selectedGroup = groups[position]

                FirestoreService.group(selectedChair!!, selectedGroup!!)
                    .get()
                    .addOnSuccessListener { result ->
                        Log.d(TAG, "The groups is blinking: ${result["is_blinking"]}")
                        selectedGroupIsBlinking = result["is_blinking"] as Boolean
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error getting the document's field.")
                    }

                FirestoreService.timetable(selectedChair!!, selectedGroup!!)
                    .get()
                    .addOnSuccessListener { result ->
                        classItems = result.toObjects(ClassItem::class.java)
                    }
                    .addOnFailureListener {
                        Log.w(TAG, "Error getting the result objects.")
                    }

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
            when {
                selectedChair == null -> {
                    spinnerChair.errorText = "Выберите кафедру"
                }
                selectedGroup == null -> {
                    spinnerGroup.errorText = "Выберите группу"
                }
                else -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.apply {
                        putExtra(CHAIR_NAME, selectedChair)
                        putExtra(GROUP_NAME, selectedGroup)
                        putExtra(IS_BLINKING, selectedGroupIsBlinking)
                        putExtra(CLASS_ITEM_ARRAY, classItems.toTypedArray())
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