package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import vn.udn.vku.hvloan.onlineshopapp.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    lateinit var topAnim: Animation
    private lateinit var bottomAnim:Animation
    private lateinit var imageView: ImageView
    private lateinit var app_name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        initComponent()
        animComponent()
        eventSplash()
    }

    fun initComponent() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        imageView = findViewById(R.id.imageView2)
        app_name = findViewById(R.id.app_name)
    }

    fun animComponent() {
        imageView.animation = topAnim
        app_name.animation = bottomAnim
    }

    fun eventSplash() {
        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }, 5000)
    }


}