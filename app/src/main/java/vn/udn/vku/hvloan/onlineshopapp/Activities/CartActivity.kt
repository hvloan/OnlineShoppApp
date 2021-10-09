package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.AppCompatButton
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Adapter.AllProductAdapter
import vn.udn.vku.hvloan.onlineshopapp.Adapter.CartAdapter
import vn.udn.vku.hvloan.onlineshopapp.Models.CartModel
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat
import java.util.ArrayList

class CartActivity : AppCompatActivity() {

    private lateinit var rcvCart: RecyclerView
    lateinit var btnPayment: AppCompatButton
    lateinit var cartModel: CartModel
    lateinit var cartAdapter: CartAdapter
    lateinit var totalCartBill: TextView
    private var decimalFormat = DecimalFormat("###,###,###")
    lateinit var arrayListCart: ArrayList<CartModel>

    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    var overTotalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initComponent()
        setupToolbar()
        getDataRcvCart()
        setupRcvCart()
        actionComponent()

    }

    private fun actionComponent() {
        btnPayment.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvCart() {
        database.collection("AddToCart").document(auth.currentUser!!.uid)
            .collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    cartModel = document.toObject(CartModel::class.java)
//                    cartModel.id = document.id
                    arrayListCart.add(cartModel)
                    cartAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun setupRcvCart() {
        cartAdapter = CartAdapter(this, arrayListCart)
        rcvCart.layoutManager = GridLayoutManager(this,1)
        rcvCart.adapter = cartAdapter

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_cart))
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setDisplayShowHomeEnabled(true);
    }

    private fun initComponent() {
        rcvCart = findViewById(R.id.rcv_cart)
        btnPayment = findViewById(R.id.btn_buy_now_cart)
        totalCartBill = findViewById(R.id.tv_total_price_cart)
        database = FirebaseFirestore.getInstance()
        arrayListCart = arrayListOf()
        auth = FirebaseAuth.getInstance()

        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiver, IntentFilter("TotalAmount"))
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(contxt: Context?, intent: Intent?) {
            val totalBill = intent!!.getIntExtra("totalAmount", 0)
            totalCartBill.text = "Total Amount: " + decimalFormat.format(totalBill) + " VNĐ"
        }
    }
}



