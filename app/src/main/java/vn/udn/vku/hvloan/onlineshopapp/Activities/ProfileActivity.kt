package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import vn.udn.vku.hvloan.onlineshopapp.Adapter.HistoryAdapter
import vn.udn.vku.hvloan.onlineshopapp.Models.HistoryModel
import vn.udn.vku.hvloan.onlineshopapp.Models.UserModel
import vn.udn.vku.hvloan.onlineshopapp.R

private const val OPEN_GALLERY_CODE = 1000
private const val OPEN_GALLERY_CODE_AVT = 100
private const val OPEN_GALLERY_CODE_COVER = 200

class ProfileActivity : AppCompatActivity() {

    private lateinit var coverUser: ImageView
    lateinit var avtUser: ImageView
    lateinit var nameUser: TextView
    lateinit var emailUser: TextView
    private lateinit var rcvHistory: RecyclerView
    private lateinit var phoneUser: TextView
    private lateinit var passwordUser: TextView
    private lateinit var btnEditCover: ImageView
    private lateinit var btnEditAvt: ImageView
    private lateinit var btnEditName: ImageView
    private lateinit var btnEditPhone: ImageView
    private lateinit var btnEditPassword: ImageView
    private lateinit var btnReturnHome: ImageView
    private lateinit var toolbarProfile: Toolbar
    lateinit var btnShowHistory: AppCompatButton
    lateinit var floatButton: FloatingActionButton
    private lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseFirestore
    lateinit var storageReference: StorageReference
    private lateinit var userModel: UserModel
    private lateinit var arrayListHistory: ArrayList<HistoryModel>
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var docIDProfile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initComponent()
        getDataProfile()
        setupToolbar()
        getDocProfile()
        actionComponent()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun actionComponent() {

        btnShowHistory.setOnClickListener {
            val array: ArrayList<HistoryModel> = ArrayList()
            database.collection("Order")
                .document(auth.currentUser!!.uid)
                .collection("DetailOrder")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val model = document.toObject(HistoryModel::class.java)
                        arrayListHistory.add(model)
                        historyAdapter.notifyDataSetChanged()
//                        findViewById<TextView>(R.id.txt_history).text = document.get("listOrder").toString()
                    }
                    Log.w("DATAA", arrayListHistory.size.toString())
                }

                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }

            historyAdapter = HistoryAdapter(this, arrayListHistory)
            rcvHistory.layoutManager = GridLayoutManager(this,1)
            rcvHistory.adapter = historyAdapter
        }

        btnReturnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnEditPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("ResetPassword")
            builder.setMessage("Please check your email to reset your password")
            builder.setIcon(android.R.drawable.ic_dialog_email)
            builder.setPositiveButton("Yes") { dialog, _ ->
//                auth.confirmPasswordReset()
                auth.sendPasswordResetEmail(userModel.email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dialog.dismiss()
                        auth.signOut()
                    }
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }

        btnEditName.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Edit Name")
            val input = EditText(this)
            input.hint = "Enter full name"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                val name = input.text.toString()
                updateName(name)
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }

        btnEditName.setOnClickListener {
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Edit Phone")
            val input = EditText(this)
            input.hint = "Enter full phone"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                val phone = input.text.toString()
                updatePhone(phone)
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        btnEditAvt.setOnClickListener {
            //open gallery
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, OPEN_GALLERY_CODE_AVT)
        }

        btnEditCover.setOnClickListener {
            val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGalleryIntent, OPEN_GALLERY_CODE_COVER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY_CODE_AVT) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data
                avtUser.setImageURI(imageUri)
                uploadAvtUserToFirebase(imageUri)
            }
        }

        if (requestCode == OPEN_GALLERY_CODE_COVER) {
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data!!.data
                coverUser.setImageURI(imageUri)
                uploadCoverUserToFirebase(imageUri)
            }
        }
    }

    private fun uploadCoverUserToFirebase(imageUri: Uri?) {
        val filePath = "Cover Images Profile Users/" + "" + auth.currentUser!!.uid
        val fileRef = storageReference.child(filePath)
        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener {
                    updateCoverUser(it.toString())
                    Glide.with(this).load(it).into(coverUser)
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun updateCoverUser(uri: String) {
        database.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("profile")
            .document(docIDProfile)
            .update(mapOf("coverImage" to uri))
            .addOnSuccessListener {
                Toast.makeText(this, "Update your cover image is successfully!!", Toast.LENGTH_SHORT).show()
                getDataProfile()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update your cover image is failed!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadAvtUserToFirebase(imageUri: Uri?) {
        //upload to fireStorage
        val filePath = "Images Profile Users/" + "" + auth.currentUser!!.uid
        val fileRef = storageReference.child(filePath)
        fileRef.putFile(imageUri!!)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener {
                    updateAvtUser(it.toString())
                    Glide.with(this).load(it).into(avtUser)
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun updateAvtUser(uri: String) {
        database.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("profile")
            .document(docIDProfile)
            .update(mapOf("avtImage" to uri))
            .addOnSuccessListener {
                Toast.makeText(this, "Update your avatar image is successfully!!", Toast.LENGTH_SHORT).show()
                getDataProfile()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update your avatar image is failed!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateName(name: String) {
        database.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("profile")
            .document(docIDProfile)
            .update(mapOf("fullName" to name))
            .addOnSuccessListener {
                Toast.makeText(this, "Update your name is successfully!!", Toast.LENGTH_SHORT).show()
                getDataProfile()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update your name is failed!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePhone(phone: String) {
        database.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("profile")
            .document(docIDProfile)
            .update(mapOf("phone" to phone))
            .addOnSuccessListener {
                Toast.makeText(this, "Update your phone is successfully!!", Toast.LENGTH_SHORT).show()
                getDataProfile()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update your phone is failed!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getDocProfile() {
        database.collection("Users")
            .document(auth.currentUser!!.uid)
            .collection("profile")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    docIDProfile = document.id
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun setupToolbar() {
        val toolbarProfile: Toolbar = findViewById(R.id.toolbar_profile)
        setSupportActionBar(toolbarProfile)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setDataProfile() {
        Glide.with(this).load(userModel.coverImage).into(coverUser)
        if (userModel.avtImage == "" || userModel.avtImage.isEmpty()) {
            userModel.avtImage = "https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png"
        }
        Glide.with(this).load(userModel.avtImage).into(avtUser)
        nameUser.text = userModel.fullName
        emailUser.text = userModel.email
        phoneUser.text = userModel.phone
        passwordUser.text = "***********"
    }

    private fun getDataProfile() {
        database.collection("Users").document(auth.currentUser!!.uid).collection("profile")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    userModel = document.toObject(UserModel::class.java)
                }
                setDataProfile()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initComponent() {
        coverUser = findViewById(R.id.img_cover)
        avtUser = findViewById(R.id.img_avt)
        nameUser = findViewById(R.id.tv_name_profile)
        emailUser = findViewById(R.id.tv_email_profile)
        phoneUser = findViewById(R.id.tv_phone_profile)
        passwordUser = findViewById(R.id.tv_pass_peofile)
        btnEditAvt = findViewById(R.id.btn_edit_avt)
        btnReturnHome = findViewById(R.id.btn_return_home)
        btnEditCover = findViewById(R.id.btn_edit_cover)
        btnEditName = findViewById(R.id.btn_edit_name)
        btnEditPhone = findViewById(R.id.btn_edit_phone)
        btnEditPassword = findViewById(R.id.btn_edit_password)
        toolbarProfile = findViewById(R.id.toolbar_profile)
        btnShowHistory = findViewById(R.id.btn_show_history)
        floatButton = findViewById(R.id.float_action)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        userModel = UserModel()
        arrayListHistory = ArrayList()
        rcvHistory = findViewById(R.id.rcv_history)
    }
}