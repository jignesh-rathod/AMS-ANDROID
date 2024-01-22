package com.app.ams.dialogs

import android.app.Activity
import android.view.KeyEvent
import android.content.Intent
import android.content.Context
import android.app.AlertDialog

import com.app.ams.activities.LoginActivity

class SessionExpireDialog
{
    companion object
    {
        fun show(context: Context)
        {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Session Expired")
            builder.setMessage("Your session has expired. Please log in again.")

            builder.setPositiveButton("Log In") { dialog, _ ->
                val editor = context.getSharedPreferences("ams", Context.MODE_PRIVATE).edit()
                editor.remove("AMS_TOKEN")
                editor.apply()

                context.startActivity(Intent(context, LoginActivity::class.java))
                if (context is Activity)
                    context.finish()

                dialog.dismiss()
            }

            val alertDialog = builder.create()

            alertDialog.setCancelable(false)
            alertDialog.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (context is Activity)
                        context.finish()
                }
                true
            }

            alertDialog.show()
        }
    }
}