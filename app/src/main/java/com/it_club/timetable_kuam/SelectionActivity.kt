package com.it_club.timetable_kuam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.it_club.timetable_kuam.utils.*
import kotlinx.android.synthetic.main.activity_group_selection.*
import kotlinx.android.synthetic.main.content_group_selection.*

class SelectionActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private var groups = listOf<String>()
    private var chair: String? = null
    private var group: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_group_selection)
        setSupportActionBar(toolbar)

        initSpinners()
        initButton()
    }

    private fun initSpinners() {
        spinnerChair.item = CHAIRS
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
                spinnerGroup.isClickable = false
                spinnerGroup.floatingLabelText = "Загружается список групп"
                spinnerGroup.showFloatingLabel()
                chair = CHAIRS[position]

                db.collection(chair!!)
                    .get()
                    .addOnSuccessListener { result ->
                        groups = result.documents.map { documentSnapshot ->
                            documentSnapshot.id
                        }
                        spinnerGroup.item = groups
                        spinnerGroup.hideFloatingLabel()
                        spinnerGroup.isClickable = true
                    }
                    .addOnFailureListener { exception ->
                        // QUESTION: It works without connection. DB is saved in cache?
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
                group = groups[position]
                spinnerChair.errorText = ""
                spinnerGroup.hideFloatingLabel()  // selecting a group floats text again
                spinnerGroup.errorText = ""
            }
        }
    }

    private fun initButton() {
        button.setOnClickListener {
            when {
                chair == null -> spinnerChair.errorText = "Выберите кафедру"
                group == null -> spinnerGroup.errorText = "Выберите группу"
                else -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(CHAIR, chair)
                    intent.putExtra(GROUP, group)
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