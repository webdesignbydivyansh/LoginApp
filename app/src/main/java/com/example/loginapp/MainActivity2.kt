package com.example.loginapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.scwang.wave.MultiWaveHeader
import com.squareup.picasso.Picasso

class MainActivity2 : AppCompatActivity() {
    private companion object{
        private const val TAG="MainActivity2"
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        auth= Firebase.auth
        val textView:TextView=findViewById(R.id.textView)
        val imgView: ImageView =findViewById(R.id.imgView)

        val value : GoogleSignInAccount = intent.getParcelableExtra("ACC")
        val name1=value.displayName
        val email1=value.email

        textView.text="Hey there, my name is $name1. If you want to contact me, my email id is $email1\nThank You!"
        Picasso.with(this).load(value.photoUrl).into(imgView)

        val waveHeader:MultiWaveHeader=findViewById(R.id.wave_header)
        val waveFooter:MultiWaveHeader=findViewById(R.id.wave_footer)

        waveHeader.setVelocity(1F)
        waveHeader.setProgress(1F)
        waveHeader.isRunning()
        waveHeader.setGradientAngle(45)
        waveHeader.setWaveHeight(40)
        waveHeader.setStartColor(Color.RED)
        waveHeader.setCloseColor(Color.BLUE)

        waveFooter.setVelocity(1F)
        waveFooter.setProgress(1F)
        waveFooter.isRunning()
        waveFooter.setGradientAngle(45)
        waveFooter.setWaveHeight(40)
        waveFooter.setStartColor(Color.GREEN)
        waveFooter.setCloseColor(Color.YELLOW)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.miLogout)
        {
            Log.i(TAG,"Logout")
            auth.signOut()
            val logoutIntent= Intent(this,MainActivity::class.java)
            logoutIntent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}




