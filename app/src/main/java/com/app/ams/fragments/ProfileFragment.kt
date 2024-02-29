package com.app.ams.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.ams.Constant
import com.app.ams.R
import com.app.ams.api.faculty.get.GetFacultyHandler
import com.app.ams.api.faculty.get.GetFacultyHandler.Companion.asGetFacultyResponse
import com.app.ams.api.profile.get.GetProfileHandler
import com.app.ams.api.profile.get.GetProfileHandler.Companion.asGetProfileResponse
import com.app.ams.api.profile.get.GetProfileResponse
import com.app.ams.dialogs.SessionExpireDialog
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ProfileFragment : Fragment()
{
    private lateinit var rootView: View
    private lateinit var context: Context

    private lateinit var profile: ConstraintLayout

    private lateinit var enrollmentCard: CardView
    private lateinit var rollNumberCard: CardView
    private lateinit var batchCard: CardView
    private lateinit var divisionCard: CardView
    private lateinit var semesterCard: CardView

    private lateinit var ivUserImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvUserType: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvContact: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvEnrollment: TextView
    private lateinit var tvRollNumber: TextView
    private lateinit var tvBatch: TextView
    private lateinit var tvDivision: TextView
    private lateinit var tvSemester: TextView

    private lateinit var btnEditProfile: Button
    private lateinit var btnUpdatePassword: Button

    private var profileData: GetProfileResponse? = null
    private var updatedProfileData: GetProfileResponse? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        context = requireContext()

        initialize()

        CoroutineScope(Dispatchers.IO).launch {
            fetchAndDisplayProfileData()
            displayProfileImage(profileData!!.imageName)
        }

        return rootView
    }

    private fun initialize()
    {
        profile = rootView.findViewById(R.id.profile)

        enrollmentCard = rootView.findViewById(R.id.enrollmentCard)
        rollNumberCard = rootView.findViewById(R.id.rollNumberCard)
        batchCard = rootView.findViewById(R.id.batchCard)
        divisionCard = rootView.findViewById(R.id.divisionCard)
        semesterCard = rootView.findViewById(R.id.semesterCard)

        ivUserImage = rootView.findViewById(R.id.ivUserImage)
        tvName = rootView.findViewById(R.id.tvName)
        tvUserType = rootView.findViewById(R.id.tvUserType)
        tvEmail = rootView.findViewById(R.id.tvEmail)
        tvContact = rootView.findViewById(R.id.tvContact)
        tvAddress = rootView.findViewById(R.id.tvAddress)
        tvEnrollment = rootView.findViewById(R.id.tvEnrollment)
        tvRollNumber = rootView.findViewById(R.id.tvRollNumber)
        tvBatch = rootView.findViewById(R.id.tvBatch)
        tvDivision = rootView.findViewById(R.id.tvDivision)
        tvSemester = rootView.findViewById(R.id.tvSemester)

        btnEditProfile = rootView.findViewById(R.id.btnEditProfile)
        btnUpdatePassword = rootView.findViewById(R.id.btnUpdatePassword)
    }

    private suspend fun fetchAndDisplayProfileData()
    {
        withContext(Dispatchers.Main) {
            val data = context.getSharedPreferences("ams", AppCompatActivity.MODE_PRIVATE).getString("USER_DATA", "").toString()
            profileData = Gson().fromJson(data, GetProfileResponse::class.java)
            updatedProfileData = profileData

            tvName.text = profileData!!.name
            tvUserType.text = profileData!!.userType
            tvEmail.text = profileData!!.email
            tvContact.text = profileData!!.contact
            tvAddress.text = profileData!!.address

            if (profileData!!.userType == Constant.USERTYPE_STUDENT)
            {
                tvEnrollment.text = profileData!!.enrollment
                tvRollNumber.text = profileData!!.rollNumber
                tvBatch.text = profileData!!.batch
                tvDivision.text = profileData!!.division
                tvSemester.text = profileData!!.semester.toString()
            }
            else
            {
                enrollmentCard.visibility = View.GONE
                rollNumberCard.visibility = View.GONE
                batchCard.visibility = View.GONE
                divisionCard.visibility = View.GONE
                semesterCard.visibility = View.GONE
            }
        }
    }

    private suspend fun displayProfileImage(imageName: String)
    {
        withContext(Dispatchers.IO) {
            try
            {
                val url = URL(Constant.UPLOADS_BASE_URL + "/images/" + imageName)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                withContext(Dispatchers.Main) {
                    ivUserImage.setImageBitmap(bmp)
                }
            } catch (e: IOException)
            {
                withContext(Dispatchers.Main) {
                    ivUserImage.setImageResource(R.drawable.ic_profile)
                }
            }
        }
    }
}