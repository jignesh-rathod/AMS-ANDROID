package com.app.ams.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.ams.Constant
import com.app.ams.R
import com.app.ams.api.profile.get.GetProfileHandler
import com.app.ams.api.profile.get.GetProfileHandler.Companion.asGetProfileResponse
import com.app.ams.dialogs.SessionExpireDialog
import com.app.ams.fragments.AttendanceFragment
import com.app.ams.fragments.HomeFragment
import com.app.ams.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity()
{
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var btnLogOut: ImageView
    private lateinit var imgProfile: ImageView

    private lateinit var homeFragment: HomeFragment
    private lateinit var attendanceFragment: AttendanceFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
//        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = getSharedPreferences("ams", MODE_PRIVATE).getString("AMS_TOKEN", "")
        if (token == "")
            logOut()

        imgProfile = findViewById(R.id.imgProfile)
        bottomNav = findViewById(R.id.bottomNav)
        btnLogOut = findViewById(R.id.btnLogOut)
        btnLogOut.setOnClickListener { logOut() }

        CoroutineScope(Dispatchers.IO).launch {
            setProfileData()

            val userType = getUserType()
            setBottomNav(userType)

            homeFragment = HomeFragment()
            attendanceFragment = AttendanceFragment()
            profileFragment = ProfileFragment()

            setCurrentFragment(homeFragment)

            bottomNav.setOnItemSelectedListener(::changeFragment)
        }
    }

    private suspend fun setProfileData()
    {
        val response = GetProfileHandler.getProfile(this@MainActivity)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(this@MainActivity)
                }
            }

            HttpURLConnection.HTTP_OK ->
            {
                val data = response.asGetProfileResponse()
                setProfileImage(data.imageName)

                withContext(Dispatchers.Main) {
                    val prefsEditor = getSharedPreferences("ams", MODE_PRIVATE).edit()

                    prefsEditor.putString("USERTYPE", data.userType)
                    prefsEditor.putString("NAME", data.name)
                    prefsEditor.putString("CONTACT", data.contact)
                    prefsEditor.putString("EMAIL", data.email)
                    prefsEditor.putString("ADDRESS", data.address)
                    prefsEditor.putString("IMAGE_NAME", data.imageName)

                    if (data.userType == Constant.USERTYPE_STUDENT)
                    {
                        prefsEditor.putString("ENROLLMENT", data.enrollment)
                        prefsEditor.putString("ROLL_NUMBER", data.rollNumber)
                        prefsEditor.putString("BATCH", data.batch)
                        prefsEditor.putString("DIVISION", data.division)
                        prefsEditor.putInt("SEMESTER", data.semester)
                    }

                    prefsEditor.apply()
                }
            }
        }
    }

    private suspend fun setProfileImage(imageName: String)
    {
        withContext(Dispatchers.IO) {
            val url = URL(Constant.UPLOADS_BASE_URL + "/images/" + imageName)
            val bmp = BitmapFactory.decodeStream(
                url.openConnection().getInputStream()
            )
            imgProfile.setImageBitmap(bmp)
        }
    }

    private suspend fun setBottomNav(userType: String)
    {
        val menu = bottomNav.menu

        withContext(Dispatchers.Main) {
            val homeMenu = menu.add(Menu.NONE, 1, 1, "Home")
            homeMenu.setIcon(R.drawable.ic_home)

            if (userType != Constant.USERTYPE_STUDENT)
            {
                val attendanceMenu = menu.add(Menu.NONE, 2, 2, "Attendance")
                attendanceMenu.setIcon(R.drawable.ic_attendance)
            }

            val profileMenu = menu.add(Menu.NONE, 3, 3, "Profile")
            profileMenu.setIcon(R.drawable.ic_profile)
        }
    }

    private suspend fun getUserType(): String
    {
        var userType: String
        withContext(Dispatchers.Main) {
            userType = getSharedPreferences("ams", MODE_PRIVATE).getString("USERTYPE", "").toString()
        }

        return userType
    }

    private fun setCurrentFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            commit()
        }
    }

    private fun changeFragment(it: MenuItem): Boolean
    {
        when (it.itemId)
        {
            1 -> setCurrentFragment(homeFragment)
            2 -> setCurrentFragment(attendanceFragment)
            3 -> setCurrentFragment(profileFragment)
        }
        return true
    }

    private fun logOut()
    {
        val editor = getSharedPreferences("ams", MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
