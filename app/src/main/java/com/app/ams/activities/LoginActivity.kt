package com.app.ams.activities

import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import android.annotation.SuppressLint

import java.net.HttpURLConnection

import com.app.ams.R
import com.app.ams.features.auth.login.LoginHandler
import com.app.ams.features.auth.login.LoginRequest
import com.app.ams.features.auth.login.LoginHandler.Companion.asLoginResponse

class LoginActivity : AppCompatActivity()
{
    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUsername = findViewById(R.id.txtUsername)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { handleLogin() }
    }

    @SuppressLint("SetTextI18n")
    private fun handleLogin()
    {
        btnLogin.isEnabled = false
        btnLogin.text = resources.getString(R.string.loading)

        val loginRequest = LoginRequest(
            username = txtUsername.text.toString(),
            password = txtPassword.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = LoginHandler.login(loginRequest)

            if (response.statusCode == HttpURLConnection.HTTP_OK)
            {
                val data = response.asLoginResponse()

                withContext(Dispatchers.Main) {
                    val editor = getSharedPreferences("ams", MODE_PRIVATE).edit()

                    editor.putString("AMS_TOKEN", data.token)
                    editor.apply()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
            else if (response.statusCode == HttpURLConnection.HTTP_FORBIDDEN)
            {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Wrong username or password",
                        Toast.LENGTH_LONG
                    ).show()

                    btnLogin.isEnabled = true
                    btnLogin.text = resources.getString(R.string.login)
                }
            }
        }
    }
}