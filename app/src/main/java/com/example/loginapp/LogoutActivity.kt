package com.example.loginapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.scwang.wave.MultiWaveHeader
import com.squareup.picasso.Picasso

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        val waveHeader: MultiWaveHeader =findViewById(R.id.wave_header)
        val waveFooter: MultiWaveHeader =findViewById(R.id.wave_footer)

        waveHeader.setVelocity(1F)
        waveHeader.setProgress(1F)
        waveHeader.isRunning()
        waveHeader.setGradientAngle(45)
        waveHeader.setWaveHeight(40)
        waveHeader.setStartColor(Color.RED)
        waveHeader.setCloseColor(Color.CYAN)

        waveFooter.setVelocity(1F)
        waveFooter.setProgress(1F)
        waveFooter.isRunning()
        waveFooter.setGradientAngle(45)
        waveFooter.setWaveHeight(40)
        waveFooter.setStartColor(Color.MAGENTA)
        waveFooter.setCloseColor(Color.YELLOW)

        val facebookName = intent.getStringExtra("facebook_name")
        val facebookPicture = intent.getStringExtra("facebook_picture")
        val facebookEmail = intent.getStringExtra("facebook_email")

        val tvView: TextView =findViewById(R.id.textView)
        tvView.text="Hey guys, my name is $facebookName. If you want to connect with me, my mail id is $facebookEmail\nThank you!"

        val imgView: ImageView =findViewById(R.id.imgView)
        Picasso.with(this).load(facebookPicture).into(imgView)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener(View.OnClickListener {
            // Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                        AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                        GraphRequest.Callback {
                            AccessToken.setCurrentAccessToken(null)
                            LoginManager.getInstance().logOut()
                            val logoutIntent= Intent(this,MainActivity::class.java)
                            logoutIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(logoutIntent)
                        }
                ).executeAsync()
            }
        })
    }
}