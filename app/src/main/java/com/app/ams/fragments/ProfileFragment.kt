package com.app.ams.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.ams.Constant
import com.app.ams.R
import com.app.ams.api.profile.get.GetProfileResponse
import com.app.ams.api.profile.update.UpdateProfileHandler
import com.app.ams.api.profile.update.UpdateProfileRequest
import com.app.ams.api.user.updatepassword.UpdatePasswordHandler
import com.app.ams.api.user.updatepassword.UpdatePasswordHandler.Companion.asUpdatePasswordResponse
import com.app.ams.api.user.updatepassword.UpdatePasswordRequest
import com.app.ams.dialogs.SessionExpireDialog
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var editProfile: ConstraintLayout
    private lateinit var updatePassword: ConstraintLayout

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

    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContact: EditText
    private lateinit var etAddress: EditText

    private lateinit var btnEditProfile: Button
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnSave: Button
    private lateinit var btnProfileCancel: Button
    private lateinit var btnSubmit: Button
    private lateinit var btnPasswordCancel: Button

    private var profileData: GetProfileResponse? = null
    private var oldProfileData: UpdateProfileRequest? = null
    private var newProfileData: UpdateProfileRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false)
        context = requireContext()

        initialize()

        btnEditProfile.setOnClickListener { handleEditProfile() }
        btnUpdatePassword.setOnClickListener { handleUpdatePassword() }
        btnSave.setOnClickListener { handleSave() }
        btnProfileCancel.setOnClickListener { handleProfileCancel() }
        btnSubmit.setOnClickListener { handleSubmit() }
        btnPasswordCancel.setOnClickListener { handlePasswordCancel() }

        etEmail.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
            }

            override fun afterTextChanged(s: Editable?)
            {
                newProfileData!!.email = s.toString()
                btnSave.isEnabled = oldProfileData != newProfileData
            }
        })

        etContact.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
            }

            override fun afterTextChanged(s: Editable?)
            {
                newProfileData!!.contact = s.toString()
                btnSave.isEnabled = oldProfileData != newProfileData
            }
        })

        etAddress.addTextChangedListener(object : TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
            {
            }

            override fun afterTextChanged(s: Editable?)
            {
                newProfileData!!.address = s.toString()
                btnSave.isEnabled = oldProfileData != newProfileData
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            fetchAndDisplayProfileData()
            displayProfileImage(profileData!!.imageName)
        }
        return rootView
    }

    private fun initialize()
    {
        profile = rootView.findViewById(R.id.profile)
        editProfile = rootView.findViewById(R.id.editProfile)
        updatePassword = rootView.findViewById(R.id.updatePassword)

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

        etCurrentPassword = rootView.findViewById(R.id.etCurrentPassword)
        etNewPassword = rootView.findViewById(R.id.etNewPassword)
        etConfirmPassword = rootView.findViewById(R.id.etConfirmPassword)
        etEmail = rootView.findViewById(R.id.etEmail)
        etContact = rootView.findViewById(R.id.etContact)
        etAddress = rootView.findViewById(R.id.etAddress)

        btnEditProfile = rootView.findViewById(R.id.btnEditProfile)
        btnUpdatePassword = rootView.findViewById(R.id.btnUpdatePassword)
        btnSave = rootView.findViewById(R.id.btnSave)
        btnProfileCancel = rootView.findViewById(R.id.btnProfileCancel)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)
        btnPasswordCancel = rootView.findViewById(R.id.btnPasswordCancel)
    }

    private fun handleEditProfile()
    {
        profile.visibility = View.GONE
        editProfile.visibility = View.VISIBLE

        etEmail.setText(oldProfileData!!.email)
        etContact.setText(oldProfileData!!.contact)
        etAddress.setText(oldProfileData!!.address)
        btnSave.isEnabled = false
    }

    private fun handleUpdatePassword()
    {
        profile.visibility = View.GONE
        updatePassword.visibility = View.VISIBLE
    }

    private fun handleSave()
    {
        CoroutineScope(Dispatchers.IO).launch {
            val response = UpdateProfileHandler.updateProfile(context, newProfileData!!)

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
                        Snackbar.make(btnSave, "profile updated successfully.", Snackbar.LENGTH_LONG).show()

                        btnSave.isEnabled = false
                        oldProfileData = UpdateProfileRequest(
                            email = newProfileData!!.email,
                            contact = newProfileData!!.contact,
                            address = newProfileData!!.address
                        )

                        profileData!!.email = oldProfileData?.email ?: ""
                        profileData!!.contact = oldProfileData?.contact ?: ""
                        profileData!!.address = oldProfileData?.address ?: ""

                        val prefsEditor = context.getSharedPreferences("ams", AppCompatActivity.MODE_PRIVATE).edit()

                        prefsEditor.putString("USER_DATA", Gson().toJson(profileData))
                        prefsEditor.apply()

                        tvEmail.text = if (oldProfileData!!.email == "") "-" else oldProfileData!!.email
                        tvContact.text = if (oldProfileData!!.contact == "") "-" else oldProfileData!!.contact
                        tvAddress.text = if (oldProfileData!!.address == "") "-" else oldProfileData!!.address

                        editProfile.visibility = View.GONE
                        profile.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handleProfileCancel()
    {
        editProfile.visibility = View.GONE
        profile.visibility = View.VISIBLE
    }

    private fun handleSubmit()
    {
        if (etCurrentPassword.text.isEmpty() ||
            etNewPassword.text.isEmpty() ||
            etConfirmPassword.text.isEmpty()
        )
        {
            Snackbar.make(btnSubmit, "please fill all fields.", Snackbar.LENGTH_LONG).show()
            return
        }

        if (etNewPassword.text.toString() != etConfirmPassword.text.toString())
        {
            Snackbar.make(btnSubmit, "new-password and confirm-password not match.", Snackbar.LENGTH_LONG).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val requestData = UpdatePasswordRequest(
                currentPassword = etCurrentPassword.text.toString(),
                newPassword = etNewPassword.text.toString(),
                confirmPassword = etConfirmPassword.text.toString()
            )
            val response = UpdatePasswordHandler.updatePassword(context, requestData)

            when (response.statusCode)
            {
                HttpURLConnection.HTTP_UNAUTHORIZED ->
                {
                    withContext(Dispatchers.Main) {
                        SessionExpireDialog.show(context)
                    }
                }

                HttpURLConnection.HTTP_FORBIDDEN ->
                {
                    val data = response.asUpdatePasswordResponse()
                    withContext(Dispatchers.Main) {
                        Snackbar.make(btnSubmit, data.error, Snackbar.LENGTH_LONG).show()
                    }
                }

                HttpURLConnection.HTTP_OK ->
                {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(btnSubmit, "password updated successfully.", Snackbar.LENGTH_LONG).show()

                        etCurrentPassword.setText("")
                        etNewPassword.setText("")
                        etConfirmPassword.setText("")

                        updatePassword.visibility = View.GONE
                        profile.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handlePasswordCancel()
    {
        updatePassword.visibility = View.GONE
        profile.visibility = View.VISIBLE
    }

    private suspend fun fetchAndDisplayProfileData()
    {
        withContext(Dispatchers.Main) {
            val data = context.getSharedPreferences("ams", AppCompatActivity.MODE_PRIVATE).getString("USER_DATA", "").toString()
            profileData = Gson().fromJson(data, GetProfileResponse::class.java)

            oldProfileData = UpdateProfileRequest(
                email = profileData?.email ?: "",
                contact = profileData?.contact ?: "",
                address = profileData?.address ?: ""
            )
            newProfileData = UpdateProfileRequest(
                email = profileData?.email ?: "",
                contact = profileData?.contact ?: "",
                address = profileData?.address ?: ""
            )

            tvName.text = profileData!!.name
            tvUserType.text = profileData!!.userType

            tvEmail.text = if (oldProfileData!!.email == "") "-" else oldProfileData!!.email
            tvContact.text = if (oldProfileData!!.contact == "") "-" else oldProfileData!!.contact
            tvAddress.text = if (oldProfileData!!.address == "") "-" else oldProfileData!!.address

            if (profileData!!.userType == Constant.USERTYPE_STUDENT)
            {
                tvEnrollment.text = profileData?.enrollment ?: "-"
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