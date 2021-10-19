package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Adapter.AddressAdapter
import vn.udn.vku.hvloan.onlineshopapp.Models.AddressModel
import vn.udn.vku.hvloan.onlineshopapp.Models.CartModel
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AddressActivity : AppCompatActivity(), AddressAdapter.SelectedAddress {

    lateinit var rcvAddress: RecyclerView
    lateinit var btnAddAddress: AppCompatButton
    lateinit var btnContinuePayment: AppCompatButton
    lateinit var toolbarAddress: Toolbar
    lateinit var database: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var addressModel: AddressModel
    lateinit var arrayListAddress: ArrayList<AddressModel>
    lateinit var addressAdapter: AddressAdapter
    var mAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        initComponent()
        setupToolbar()
        actionComponent()
        getDataRcvAddress()
        setupRcvAddress()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun actionComponent() {
        btnAddAddress.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("ADD ADDRESS")
            val dialogLayout = inflater.inflate(R.layout.alert_add_address, null)

            val editProvince  = dialogLayout.findViewById<EditText>(R.id.txt_province)
            val editDistrict = dialogLayout.findViewById<EditText>(R.id.txt_district)
            val editCommune = dialogLayout.findViewById<EditText>(R.id.txt_commnune)
            val editVillage = dialogLayout.findViewById<EditText>(R.id.txt_village)
            val editName = dialogLayout.findViewById<EditText>(R.id.txt_name)
            val btnOkeAdd = dialogLayout.findViewById<AppCompatButton>(R.id.btn_oke_add_address)
            var finalAddress = ""

            btnOkeAdd.setOnClickListener {

                val province = editProvince.text.toString()
                val district = editDistrict.text.toString()
                val commune = editCommune.text.toString()
                val village = editVillage.text.toString()
                val name = editName.text.toString()

                if (name.isNotEmpty()) {
                    finalAddress += "$name, "
                }
                if (village.isNotEmpty()) {
                    finalAddress += "$village, "
                }
                if (commune.isNotEmpty()) {
                    finalAddress += "$commune, "
                }
                if (district.isNotEmpty()) {
                    finalAddress += "$district, "
                }
                if (province.isNotEmpty()) {
                    finalAddress += "$province."
                }

                if (province.isNotEmpty() && district.isNotEmpty() && commune.isNotEmpty() && village.isNotEmpty() && name.isNotEmpty()) {
                    val hashMap = HashMap<String, String>()
                    hashMap["address"] = finalAddress


                    database.collection("Users").document(auth.currentUser!!.uid)
                        .collection("address").add(hashMap).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Address is added!!!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AddressActivity::class.java))
                            }
                        }
                }
            }

            builder.setView(dialogLayout)
            builder.show()

        }

        btnContinuePayment.setOnClickListener {
            if (intent.extras != null) {
                getDataTransfer()
            } else {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("dataAddress", mAddress)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDataTransfer() {
        val currentDate: String
        val productName: String
        val productPrice: Int
        val productNumber: Int
        val currentTime: String
        val totalPrice: Int
        if (intent.extras != null) {
            currentDate = intent.extras?.getString("currentDate").toString()
            currentTime = intent.extras?.getString("currentTime").toString()
            productName = intent.extras?.getString("productName").toString()
            productPrice = intent.extras?.getInt("productPrice")!!.toInt()
            productNumber = intent.extras?.getInt("productNumber")!!.toInt()
            totalPrice = productPrice * productNumber
            //payment
            val calForDate: Calendar = Calendar.getInstance()
            val mCurrentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss a")
            val arrayList = arrayListOf<CartModel>(
                CartModel(currentDate, currentTime, productName, productNumber, productPrice, totalPrice)
            )
            val hashMapPayment = HashMap<String, Any>()
            hashMapPayment["listOrder"] = arrayList
            hashMapPayment["addressDelivery"] = mAddress
            hashMapPayment["timeOrder"] = mCurrentDate.format(calForDate.time).toString()
            database.collection("Order")
                .document(auth.currentUser!!.uid).collection("DetailOrder")
                .add(hashMapPayment).addOnCompleteListener {
                    val col = database.collection("AddToCart").document(auth.currentUser!!.uid).collection("User")
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
    }

    private fun setupRcvAddress() {
        addressAdapter = AddressAdapter(applicationContext, arrayListAddress, this)
        rcvAddress.layoutManager = GridLayoutManager(this,1)
        rcvAddress.adapter = addressAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvAddress() {
        database.collection("Users").document(auth.currentUser!!.uid).collection("address")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    addressModel = document.toObject(AddressModel::class.java)
                    arrayListAddress.add(addressModel)
                    addressAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbarAddress)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setDisplayShowHomeEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initComponent() {
        rcvAddress = findViewById(R.id.rcv_address)
        btnAddAddress = findViewById(R.id.btn_add_address)
        btnContinuePayment = findViewById(R.id.btn_continue_payment)
        toolbarAddress = findViewById(R.id.toolbar_address)

        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        arrayListAddress = ArrayList()
    }

    override fun setAddress(address: String) {
        mAddress = address
    }

}

