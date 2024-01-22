package com.app.ams.activities

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Button
import android.content.Intent

import com.app.ams.R

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = getSharedPreferences("ams", MODE_PRIVATE).getString("AMS_TOKEN", "")
        if (token == "")
            goToLogin()

        val btnLogout = findViewById<Button>(R.id.btnLogin)
        btnLogout.setOnClickListener { logOut() }
    }

    private fun goToLogin()
    {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun logOut()
    {
        val editor = getSharedPreferences("ams", MODE_PRIVATE).edit()
        editor.remove("AMS_TOKEN")
        editor.apply()

        goToLogin()
    }
}
