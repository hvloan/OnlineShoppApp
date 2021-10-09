package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Models.UserModel
import vn.udn.vku.hvloan.onlineshopapp.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var imgcloud01: ImageView
    private lateinit var imgcloud02: ImageView
    private lateinit var imgcloud03: ImageView
    private lateinit var imgcloud04: ImageView
    private lateinit var animCloud: Animation

    private lateinit var txtUsername: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPhoneNumber: EditText

    private lateinit var btnRegister: AppCompatButton
    private lateinit var btnLogin: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initComponent()

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
        txtEmail = findViewById(R.id.email_txt)
        txtPhoneNumber = findViewById(R.id.phone_txt)

        btnLogin = findViewById(R.id.login_btnn)
        btnRegister = findViewById(R.id.register_btnn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        dialog = ProgressDialog(this)
    }

    private fun animComponent() {
        imgcloud01.startAnimation(animCloud)
        imgcloud02.startAnimation(animCloud)
        imgcloud03.startAnimation(animCloud)
        imgcloud04.startAnimation(animCloud)
    }

    private fun getTextUsername(): String {
        return txtUsername.text.toString().trim()
    }

    private fun getTextPassword(): String {
        return txtPassword.text.toString().trim()
    }

    private fun getTextEmail(): String {
        return txtEmail.text.toString().trim()
    }

    private fun getTextPhoneNumber(): String {
        return txtPhoneNumber.text.toString().trim()
    }

    private fun checkForm(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }

    private fun actionComponent() {
        btnLogin.setOnClickListener {
            dialog.setTitle("Register")
            dialog.setMessage("Application is logging, please wait@@")
            dialog.show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            dialog.setTitle("Register")
            dialog.setMessage("Application is signing, please wait@@")
            dialog.show()
            addNewUser()
        }
    }

    private fun addNewUser() {
        if (!checkForm(getTextEmail())) {
            Toast.makeText(this, "Please enter your email @@", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkForm(getTextPassword())) {
            Toast.makeText(this, "Please enter your password @@", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkForm(getTextPhoneNumber())) {
            Toast.makeText(this, "Please enter your phone number. It's very important!!  @@", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkForm(getTextUsername())) {
            Toast.makeText(this, "Please enter your user name @@", Toast.LENGTH_SHORT).show()
            return
        }

        if (getTextPassword().length < 6) {
            Toast.makeText(this, "Password too short, pls enter minimum 6 characters @@", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(getTextEmail(), getTextPassword()).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Successfully!!!", Toast.LENGTH_SHORT).show()

                val userModel = UserModel(
                    getTextUsername(),
                    getTextEmail(),
                    getTextPhoneNumber(),
//                    getTextPassword(),
                    "",
                    ""
                )

                database.collection("Users").document(auth.currentUser!!.uid).collection("profile")
                    .add(userModel)
                    .addOnSuccessListener {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        Log.d("SIGNUP ERROR: ", e.message.toString())
                        Toast.makeText(this, "Register failed!!!", Toast.LENGTH_SHORT).show()
                    }

            } else {
                Toast.makeText(this, "Register failed!!!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}