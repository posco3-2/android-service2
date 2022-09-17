package com.posco.posco_store.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.posco.posco_store.R

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handler = Handler()

        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)


    }

}