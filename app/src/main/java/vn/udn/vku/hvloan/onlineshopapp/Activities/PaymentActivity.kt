package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import vn.udn.vku.hvloan.onlineshopapp.Adapter.AddressAdapter
import vn.udn.vku.hvloan.onlineshopapp.Adapter.CartAdapter
import vn.udn.vku.hvloan.onlineshopapp.Models.CartModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PaymentActivity : AppCompatActivity() {

    private lateinit var btnConfirm: AppCompatButton
    private lateinit var rcvPayment: RecyclerView
    lateinit var addressDelivery: TextView
    lateinit var toolbarPayment: Toolbar
    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseFirestore
    lateinit var cartModel: CartModel
    lateinit var arrayListCart: ArrayList<CartModel>
    private lateinit var cartAdapter: CartAdapter
    var addressUser = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initComponent()
        setupToolbar()
        getDataRcvPayment()
        setupRcvPayment()
        actionComponent()

    }

    @SuppressLint("SimpleDateFormat")
    private fun actionComponent() {
        btnConfirm.setOnClickListener {

            val calForDate: Calendar = Calendar.getInstance()
            val currentDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss a")

            val hashMapPayment = HashMap<String, Any>()
            hashMapPayment["listOrder"] = arrayListCart
            hashMapPayment["addressDelivery"] = addressUser
            hashMapPayment["timeOrder"] = currentDate.format(calForDate.time).toString()
            database.collection("Order")
                .document(auth.currentUser!!.uid).collection("DetailOrder")
                .add(hashMapPayment).addOnCompleteListener {
                    val col = database.collection("AddToCart").document(auth.currentUser!!.uid).collection("User")
                    val size = arrayListCart.size
                    deleteCollection(col,size)
                    startActivity(Intent(this, MainActivity::class.java))

                }
        }
    }

    private fun deleteCollection(collection: CollectionReference, batchSize: Int) {
        try {
            // Retrieve a small batch of documents to avoid out-of-memory errors/
            var deleted = 0
            collection
                .limit(batchSize.toLong())
                .get()
                .addOnCompleteListener {
                    for (document in it.result!!.documents) {
                        document.reference.delete()
                        ++deleted
                    }
                    if (deleted >= batchSize) {
                        // retrieve and delete another batch
                        deleteCollection(collection, batchSize)
                    }
                }
        } catch (e: Exception) {
            System.err.println("Error deleting collection : " + e.message)
        }
    }

    private fun setupRcvPayment() {
        cartAdapter = CartAdapter(this, arrayListCart)
        rcvPayment.layoutManager = GridLayoutManager(this,1)
        rcvPayment.adapter = cartAdapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvPayment() {
        database.collection("AddToCart")
            .document(auth.currentUser!!.uid)
            .collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    cartModel = document.toObject(CartModel::class.java)
                    addressDelivery.text = addressUser
                    arrayListCart.add(cartModel)
                    cartAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbarPayment)
        val actionBar: ActionBar? = supportActionBar
        supportActionBar?.title = "Payment"
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun initComponent() {
        btnConfirm = findViewById(R.id.btn_confirm)
        rcvPayment = findViewById(R.id.rcy_payment)
        toolbarPayment = findViewById(R.id.toolbar_payment)
        addressDelivery = findViewById(R.id.tv_address_delivery)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        arrayListCart = ArrayList()
        if (intent != null) {
            addressUser = intent.extras!!.getString("dataAddress").toString()
        }
    }
}