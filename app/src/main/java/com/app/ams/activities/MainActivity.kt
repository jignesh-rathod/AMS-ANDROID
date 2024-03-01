package com.app.ams.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.app.ams.Constant
import com.app.ams.R
import com.app.ams.api.profile.get.GetProfileHandler
import com.app.ams.api.profile.get.GetProfileHandler.Companion.asGetProfileResponse
import com.app.ams.api.profile.get.GetProfileResponse
import com.app.ams.dialogs.SessionExpireDialog
import com.app.ams.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity()
{
    private lateinit var btnLogOut: ImageView
    private lateinit var ivUserImage: ImageView
    private lateinit var bottomNav: BottomNavigationView

    private lateinit var dashboardFragment: DashboardFragment
    private lateinit var attendanceFragment: AttendanceFragment
    private lateinit var studentDashboardFragment: StudentDashboardFragment
    private lateinit var profileFragment: ProfileFragment

    private var profileData: GetProfileResponse? = null
    private var isAuthorized: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = getSharedPreferences("ams", MODE_PRIVATE).getString("AMS_TOKEN", "")
        if (token == "")
        {
            logOut()
            return
        }

        ivUserImage = findViewById(R.id.ivUserImage)
        bottomNav = findViewById(R.id.bottomNav)
        btnLogOut = findViewById(R.id.btnLogOut)
        btnLogOut.setOnClickListener { logOut() }

        CoroutineScope(Dispatchers.IO).launch {
            fetchProfileData()

            if (!isAuthorized)
                return@launch

            displayProfileImage(profileData!!.imageName)

            val userType = profileData!!.userType
            setBottomNav(userType)

            profileFragment = ProfileFragment()
            if (userType == Constant.USERTYPE_STUDENT)
            {
                studentDashboardFragment = StudentDashboardFragment()
                setCurrentFragment(studentDashboardFragment)
            }
            else
            {
                dashboardFragment = DashboardFragment()
                attendanceFragment = AttendanceFragment()
                setCurrentFragment(dashboardFragment)
            }

            bottomNav.setOnItemSelectedListener(::changeFragment)
        }
    }

    private suspend fun fetchProfileData()
    {
        val response = GetProfileHandler.getProfile(this@MainActivity)

        when (response.statusCode)
        {
            HttpURLConnection.HTTP_UNAUTHORIZED ->
            {
                withContext(Dispatchers.Main) {
                    SessionExpireDialog.show(this@MainActivity)
                }
                isAuthorized = false
            }

            HttpURLConnection.HTTP_OK ->
            {
                profileData = response.asGetProfileResponse()

                withContext(Dispatchers.Main) {
                    isAuthorized = true
                    val prefsEditor = getSharedPreferences("ams", MODE_PRIVATE).edit()

                    prefsEditor.putString("USER_DATA", Gson().toJson(profileData))
                    prefsEditor.apply()
                }
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

    private suspend fun setBottomNav(userType: String)
    {
        val menu = bottomNav.menu

        withContext(Dispatchers.Main) {
            val dashboardMenu = menu.add(Menu.NONE, 1, 1, "Dashboard")
            dashboardMenu.setIcon(R.drawable.ic_dashboard)

            if (userType != Constant.USERTYPE_STUDENT)
            {
                val attendanceMenu = menu.add(Menu.NONE, 2, 2, "Attendance")
                attendanceMenu.setIcon(R.drawable.ic_attendance)
            }

            val profileMenu = menu.add(Menu.NONE, 3, 3, "Profile")
            profileMenu.setIcon(R.drawable.ic_profile)
        }
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
        if (profileData!!.userType == Constant.USERTYPE_STUDENT)
        {
            when (it.itemId)
            {
                1 -> setCurrentFragment(studentDashboardFragment)
                3 -> setCurrentFragment(profileFragment)
            }
        }
        else
        {
            when (it.itemId)
            {
                1 -> setCurrentFragment(dashboardFragment)
                2 -> setCurrentFragment(attendanceFragment)
                3 -> setCurrentFragment(profileFragment)
            }
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
