package com.app.ams.activities

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.EditText

import java.net.HttpURLConnection

import com.app.ams.R
import com.app.ams.api.auth.login.LoginHandler
import com.app.ams.api.auth.login.LoginRequest
import com.app.ams.api.auth.login.LoginHandler.Companion.asLoginResponse
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { handleLogin() }
    }

    private fun handleLogin()
    {
        btnLogin.isEnabled = false
        btnLogin.text = resources.getString(R.string.loading)

        val loginRequest = LoginRequest(
            username = etUsername.text.toString(),
            password = etPassword.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = LoginHandler.login(loginRequest)

            if (response.statusCode == HttpURLConnection.HTTP_OK)
            {
                val data = response.asLoginResponse()

                withContext(Dispatchers.Main) {
                    val prefsEditor = getSharedPreferences("ams", MODE_PRIVATE).edit()

                    prefsEditor.putString("AMS_TOKEN", data.token)
                    prefsEditor.apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
            else if (response.statusCode == HttpURLConnection.HTTP_FORBIDDEN)
            {
                withContext(Dispatchers.Main) {
                    Snackbar.make(btnLogin, "Wrong username or password", Snackbar.LENGTH_LONG).show()

                    btnLogin.isEnabled = true
                    btnLogin.text = resources.getString(R.string.login)
                }
            }
        }
    }
}