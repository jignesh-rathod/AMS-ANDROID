package com.app.ams.fragments

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.widget.DatePicker
import android.widget.AdapterView
import android.view.LayoutInflater
import android.app.DatePickerDialog
import android.widget.AutoCompleteTextView
import android.app.DatePickerDialog.OnDateSetListener

import java.util.*
import java.net.HttpURLConnection

import com.app.ams.R
import com.app.ams.adapters.BatchAdapter
import com.app.ams.adapters.LectureAdapter
import com.app.ams.adapters.SemesterAdapter
import com.app.ams.dialogs.SessionExpireDialog
import com.app.ams.api.batch.get.GetBatchHandler
import com.app.ams.api.lecture.get.GetLectureHandler
import com.app.ams.api.semester.get.GetSemesterHandler
import com.app.ams.api.batch.get.GetBatchHandler.Companion.asGetBatchResponse
import com.app.ams.api.lecture.get.GetLectureHandler.Companion.asGetLectureResponse
import com.app.ams.api.semester.get.GetSemesterHandler.Companion.asGetSemesterResponse

import com.google.android.material.textfield.TextInputEditText

class AttendanceFragment : Fragment()
{
    private lateinit var ddBatch: AutoCompleteTextView
    private lateinit var ddSemester: AutoCompleteTextView
    private lateinit var ddDivision: AutoCompleteTextView
    private lateinit var ddSubject: AutoCompleteTextView
    private lateinit var ddTime: AutoCompleteTextView
    private lateinit var etDate: TextInputEditText

    private lateinit var dateSetListener: OnDateSetListener

    private lateinit var rootView: View
    private lateinit var context: Context

    private var oldSelectedBatchPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_attendance, container, false)
        context = requireContext()

        ddBatch = rootView.findViewById(R.id.ddBatch)
        ddSemester = rootView.findViewById(R.id.ddSemester)
        ddDivision = rootView.findViewById(R.id.ddDivision)
        ddSubject = rootView.findViewById(R.id.ddSubject)
        ddTime = rootView.findViewById(R.id.ddTime)
        etDate = rootView.findViewById(R.id.etDate)

        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$day/${month + 1}/$year"

        etDate.setText(currentDate)
        etDate.setOnClickListener { showDatePickerDialog(day, month, year) }

        dateSetListener = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
            etDate.setText(selectedDate)
        }

        CoroutineScope(Dispatchers.IO).launch {
            getBatches()
            getLectures()
        }

        return rootView
    }

    private fun showDatePickerDialog(day: Int, month: Int, year: Int)
    {
        val datePickerDialog = DatePickerDialog(
            context,
            dateSetListener,
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private suspend fun getBatches()
    {
        val response = GetBatchHandler.getBatches(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetBatchResponse()
                var batchAdapter: BatchAdapter

                withContext(Dispatchers.Main) {
                    batchAdapter = BatchAdapter(context, data.batches)
                    ddBatch.setAdapter(batchAdapter)

                }

                ddBatch.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    if (oldSelectedBatchPosition != position)
                    {
                        ddSemester.setText("")
                        ddDivision.setText("")
                        ddSubject.setText("")
                        oldSelectedBatchPosition = position
                        val selectedBatchId = batchAdapter.getBatchId(position)

                        CoroutineScope(Dispatchers.IO).launch {
                            getSemesters(selectedBatchId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getLectures()
    {
        val response = GetLectureHandler.getLectures(context)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetLectureResponse()
                var lectureAdapter: LectureAdapter

                withContext(Dispatchers.Main) {
                    lectureAdapter = LectureAdapter(context, data.lectures)
                    ddTime.setAdapter(lectureAdapter)
                }
            }
        }
    }

    private suspend fun getSemesters(batchId: Int)
    {
        val response = GetSemesterHandler.getSemesters(context, batchId)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(context)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetSemesterResponse()
                var semesterAdapter: SemesterAdapter

                withContext(Dispatchers.Main) {
                    semesterAdapter = SemesterAdapter(context, data.semesters)
                    ddSemester.setAdapter(semesterAdapter)
                }

                ddSemester.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val selectedSemesterId = semesterAdapter.getSemesterId(position)
                    val selectedSemester = semesterAdapter.getItem(position)
                }
            }
        }
    }
}