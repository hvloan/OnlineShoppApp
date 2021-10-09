package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Models.UserModel
import vn.udn.vku.hvloan.onlineshopapp.R


class LoginActivity : AppCompatActivity() {

    private lateinit var imgcloud01: ImageView
    private lateinit var imgcloud02: ImageView
    private lateinit var imgcloud03: ImageView
    private lateinit var imgcloud04: ImageView
    private lateinit var animCloud: Animation

    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnForgotPass: TextView
    private lateinit var btnLogin: AppCompatButton
    private lateinit var btnRegister: AppCompatButton

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseFirestore

    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initComponent()


        if(auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        animComponent()
        actionComponent()
    }

    private fun initComponent() {
        imgcloud01 = findViewById(R.id.cloud_img01)
        imgcloud02 = findViewById(R.id.cloud_img02)
        imgcloud03 = findViewById(R.id.cloud_img03)
        imgcloud04 = findViewById(R.id.cloud_img04)
        animCloud = AnimationUtils.loadAnimation(this, R.anim.animcloud)

        txtUsername = findViewById(R.id.username_txt)
        txtPassword = findViewById(R.id.password_txt)
        btnForgotPass = findViewById(R.id.forgot_pass_btn)
        btnLogin = findViewById(R.id.register_btnn)
        btnRegister = findViewById(R.id.register_btn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        userModel = UserModel()
    }

    private fun animComponent() {
        imgcloud01.startAnimation(animCloud)
        imgcloud02.startAnimation(animCloud)
        imgcloud03.startAnimation(animCloud)
        imgcloud04.startAnimation(animCloud)
    }

    private fun getTextUsername(): String {
        return txtUsername.text.toString()
    }

    private fun getTextPassword(): String {
        return txtPassword.text.toString()
    }

    private fun checkForm(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }

    private fun actionComponent() {
        btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            loginWithUser()
        }
    }

    private fun loginWithUser() {
        if (!checkForm(getTextUsername())) {
            Toast.makeText(this, "Please enter your email @@", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkForm(getTextPassword())) {
            Toast.makeText(this, "Please enter your password @@", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(getTextUsername(), getTextPassword()).addOnCompleteListener {
            if (it.isSuccessful) {
                database.collection("Users").whereEqualTo("email", getTextUsername())
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        userModel = document.toObject(UserModel::class.java)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
                Toast.makeText(this, "Successfully!!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Login failed!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}