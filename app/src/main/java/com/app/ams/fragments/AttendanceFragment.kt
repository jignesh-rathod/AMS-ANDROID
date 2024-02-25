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
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import java.util.*
import java.net.HttpURLConnection

import com.app.ams.R
import com.app.ams.adapters.*
import com.app.ams.api.attendance.add.AddAttendanceHandler
import com.app.ams.api.attendance.add.AddAttendanceRequest
import com.app.ams.dialogs.SessionExpireDialog
import com.app.ams.api.batch.get.GetBatchHandler
import com.app.ams.api.lecture.get.GetLectureHandler
import com.app.ams.api.semester.get.GetSemesterHandler
import com.app.ams.api.batch.get.GetBatchHandler.Companion.asGetBatchResponse
import com.app.ams.api.division.get.GetDivisionHandler
import com.app.ams.api.division.get.GetDivisionHandler.Companion.asGetDivisionResponse
import com.app.ams.api.lecture.get.GetLectureHandler.Companion.asGetLectureResponse
import com.app.ams.api.semester.get.GetSemesterHandler.Companion.asGetSemesterResponse
import com.app.ams.api.student.getforattendance.GetStudentForAttendanceHandler
import com.app.ams.api.student.getforattendance.GetStudentForAttendanceHandler.Companion.asGetStudentForAttendanceResponse
import com.app.ams.api.student.getforattendance.models.Student
import com.app.ams.api.subject.get.GetSubjectHandler
import com.app.ams.api.subject.get.GetSubjectHandler.Companion.asGetSubjectResponse
import com.google.android.material.snackbar.Snackbar

import com.google.android.material.textfield.TextInputEditText
import kotlin.collections.ArrayList

class AttendanceFragment : Fragment(), StudentForAttendanceRVAdapter.OnCheckedChangeListener
{
    private lateinit var progressBar: ProgressBar
    private lateinit var addAttendanceForm: ConstraintLayout
    private lateinit var insertAttendanceForm: ConstraintLayout

    private lateinit var ddBatch: AutoCompleteTextView
    private lateinit var ddSemester: AutoCompleteTextView
    private lateinit var ddDivision: AutoCompleteTextView
    private lateinit var ddSubject: AutoCompleteTextView
    private lateinit var ddTime: AutoCompleteTextView
    private lateinit var etDate: TextInputEditText
    private lateinit var btnAdd: Button

    private lateinit var rvStudents: RecyclerView
    private lateinit var btnInsert: Button
    private lateinit var btnCancel: Button

    private lateinit var dateSetListener: OnDateSetListener

    private lateinit var rootView: View
    private lateinit var context: Context

    private var oldSelectedBatchPosition: Int = -1
    private var oldSelectedSemesterPosition: Int = -1

    private var selectedBatchId: Int? = null
    private var selectedSemesterId: Int? = null
    private var selectedDivisionId: Int? = null
    private var selectedSubjectId: Int? = null
    private var selectedTimeId: Int? = null
    private lateinit var selectedDate: String

    private var studentDataForInsertAttendance: ArrayList<Student>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_attendance, container, false)
        context = requireContext()

        progressBar = rootView.findViewById(R.id.progressBar)
        addAttendanceForm = rootView.findViewById(R.id.addAttendanceForm)
        insertAttendanceForm = rootView.findViewById(R.id.insertAttendanceForm)

        ddBatch = rootView.findViewById(R.id.ddBatch)
        ddSemester = rootView.findViewById(R.id.ddSemester)
        ddDivision = rootView.findViewById(R.id.ddDivision)
        ddSubject = rootView.findViewById(R.id.ddSubject)
        ddTime = rootView.findViewById(R.id.ddTime)
        etDate = rootView.findViewById(R.id.etDate)
        btnAdd = rootView.findViewById(R.id.btnAdd)

        rvStudents = rootView.findViewById(R.id.rvStudents)
        btnInsert = rootView.findViewById(R.id.btnInsert)
        btnCancel = rootView.findViewById(R.id.btnCancel)

        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$day/${month + 1}/$year"

        selectedDate = "$year-$month-$day"
        etDate.setText(currentDate)
        etDate.setOnClickListener { showDatePickerDialog(day, month, year) }

        dateSetListener = OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
            this.selectedDate = "$year-$monthOfYear-$dayOfMonth"
            etDate.setText(selectedDate)
        }

        btnAdd.setOnClickListener { handleAdd() }
        btnInsert.setOnClickListener { handleInsert() }
        btnCancel.setOnClickListener { handleCancel() }

        CoroutineScope(Dispatchers.IO).launch {
            fetchBatches()
            fetchLectures()
        }

        return rootView
    }

    override fun onCheckedChange(position: Int, isChecked: Boolean)
    {
        if (studentDataForInsertAttendance != null)
            studentDataForInsertAttendance!![position].isPresent = isChecked
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

    private fun handleAdd()
    {
        if (selectedBatchId == null ||
            selectedSemesterId == null ||
            selectedDivisionId == null ||
            selectedSubjectId == null ||
            selectedTimeId == null
        )
        {
            Snackbar.make(btnAdd, "Please fill all fields.", Snackbar.LENGTH_LONG).show()
            return
        }
        fetchStudentsForAttendance(selectedBatchId!!, selectedSemesterId!!, selectedDivisionId!!)
    }

    private fun handleInsert()
    {
        val addAttendanceRequest = AddAttendanceRequest(
            divisionId = selectedDivisionId!!,
            subjectId = selectedSubjectId!!,
            lectureId = selectedTimeId!!,
            date = selectedDate,
            studentRecords = studentDataForInsertAttendance!!
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = AddAttendanceHandler.addAttendance(context, addAttendanceRequest)

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
                    withContext(Dispatchers.Main) {
                        Snackbar.make(btnInsert, "Attendance Added Successfully.", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun handleCancel()
    {
        insertAttendanceForm.visibility = View.GONE
        addAttendanceForm.visibility = View.VISIBLE
    }

    private suspend fun fetchBatches()
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

                        selectedSemesterId = null
                        selectedDivisionId = null
                        selectedSubjectId = null

                        oldSelectedSemesterPosition = -1
                        oldSelectedBatchPosition = position
                        selectedBatchId = batchAdapter.getBatchId(position)

                        val selectedBatchId = batchAdapter.getBatchId(position)

                        CoroutineScope(Dispatchers.IO).launch {
                            fetchSemesters(selectedBatchId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchLectures()
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

                ddTime.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    selectedTimeId = lectureAdapter.getLectureId(position)
                }
            }
        }
    }

    private suspend fun fetchSemesters(batchId: Int)
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
                    if (oldSelectedSemesterPosition != position)
                    {
                        ddDivision.setText("")
                        ddSubject.setText("")

                        selectedDivisionId = null
                        selectedSubjectId = null

                        oldSelectedSemesterPosition = position
                        selectedSemesterId = semesterAdapter.getSemesterId(position)

                        val selectedSemesterId = semesterAdapter.getSemesterId(position)

                        CoroutineScope(Dispatchers.IO).launch {
                            fetchDivisionsAndSubjects(selectedSemesterId)
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchDivisionsAndSubjects(semesterId: Int)
    {
        val divisionResponse = GetDivisionHandler.getDivisions(context, semesterId)
        val subjectResponse = GetSubjectHandler.getSubjects(context, semesterId)

        if (divisionResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED && subjectResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
        {
            withContext(Dispatchers.Main) {
                SessionExpireDialog.show(context)
            }
        }
        else if (divisionResponse.statusCode == HttpURLConnection.HTTP_OK && subjectResponse.statusCode == HttpURLConnection.HTTP_OK)
        {
            val divisionData = divisionResponse.asGetDivisionResponse()
            val subjectData = subjectResponse.asGetSubjectResponse()

            var divisionAdapter: DivisionAdapter
            var subjectAdapter: SubjectAdapter

            withContext(Dispatchers.Main) {
                divisionAdapter = DivisionAdapter(context, divisionData.divisions)
                ddDivision.setAdapter(divisionAdapter)

                subjectAdapter = SubjectAdapter(context, subjectData.subjects)
                ddSubject.setAdapter(subjectAdapter)
            }

            ddDivision.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedDivisionId = divisionAdapter.getDivisionId(position)
            }

            ddSubject.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedSubjectId = subjectAdapter.getSubjectId(position)
            }
        }
    }

    private fun fetchStudentsForAttendance(batchId: Int, semesterId: Int, divisionId: Int)
    {
        addAttendanceForm.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        btnInsert.isEnabled = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = GetStudentForAttendanceHandler.getStudents(context, batchId, semesterId, divisionId)

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
                    val data = response.asGetStudentForAttendanceResponse()
                    var studentForAttendanceRVAdapter: StudentForAttendanceRVAdapter
                    studentDataForInsertAttendance = data.students

                    withContext(Dispatchers.Main) {
                        studentForAttendanceRVAdapter = StudentForAttendanceRVAdapter(context, R.layout.student_list_item_for_attendance, data.students, this@AttendanceFragment)

                        rvStudents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        rvStudents.adapter = studentForAttendanceRVAdapter

                        if (studentForAttendanceRVAdapter.itemCount < 1)
                            btnInsert.isEnabled = false

                        progressBar.visibility = View.GONE
                        insertAttendanceForm.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}