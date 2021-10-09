package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.Models.UserModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailActivity : AppCompatActivity() {

    private lateinit var imgProduct: ImageView
    private lateinit var nameProduct: TextView
    private lateinit var starProduct: TextView
    private lateinit var descProduct: TextView
    private lateinit var priceProduct: TextView
    private lateinit var numberProduct: TextView
    private lateinit var btnMinus: ImageView
    private lateinit var btnPlus: ImageView
    private lateinit var btnAddToCart: AppCompatButton
    private lateinit var btnBuyNow: AppCompatButton
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var decimalFormat = DecimalFormat("###,###,###")

    lateinit var namePro: String
    private lateinit var imagePro: String
    var pricePro = 0
    lateinit var descPro: String
    lateinit var ratingPro: String
    var numPro = 1
    lateinit var idProduct: String
    var quantityProduct = 0

    lateinit var productModel: ProductModel
    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initComponent()
        setupToolbar()
        getData()
        setupDataComponent()
        actionComponent()
    }

    @SuppressLint("SimpleDateFormat")
    private fun actionComponent() {
        btnMinus.setOnClickListener {
            if (numPro < 2) {
                btnMinus.isVisible = false
            } else {
                btnPlus.isVisible = true
                numPro--
                val priceProNew = pricePro * numPro
                numberProduct.text = numPro.toString()
                priceProduct.text = decimalFormat.format(priceProNew).toString().plus(" VNĐ")
            }
        }

        btnPlus.setOnClickListener {
            if (numPro > quantityProduct -1) {
                btnPlus.isVisible = false
            } else {
                btnMinus.isVisible = true
                numPro++
                val priceProNew = pricePro * numPro
                numberProduct.text = numPro.toString()
                priceProduct.text = decimalFormat.format(priceProNew).toString().plus(" VNĐ")
            }
        }

        btnAddToCart.setOnClickListener {
            addToCart()
        }

        btnBuyNow.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            val saveCurrentDate: String
            val saveCurrentTime: String
            val calForDate: Calendar = Calendar.getInstance()

            val currentDate = SimpleDateFormat("dd-MM-yyyy")
            saveCurrentDate = currentDate.format(calForDate.time).toString()

            val currentTime = SimpleDateFormat("HH:mm:ss a")
            saveCurrentTime = currentTime.format(calForDate.time).toString()

            intent.putExtra("productName", nameProduct.text.toString())
            intent.putExtra("productPrice", pricePro)
            intent.putExtra("currentTime", saveCurrentTime)
            intent.putExtra("currentDate", saveCurrentDate)
            intent.putExtra("productNumber", numberProduct.text.toString().toInt())

            startActivity(intent)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addToCart() {
        val saveCurrentDate: String
        val saveCurrentTime: String
        val calForDate: Calendar = Calendar.getInstance()

        val currentDate = SimpleDateFormat("dd-MM-yyyy")
        saveCurrentDate = currentDate.format(calForDate.time).toString()

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentTime.format(calForDate.time).toString()

        val hashMapCart: HashMap<String, Any> = HashMap()
        hashMapCart["productName"] = nameProduct.text.toString()
        hashMapCart["productPrice"] = pricePro
        hashMapCart["productNumber"] = numberProduct.text.toString().toInt()
        hashMapCart["currentTime"] = saveCurrentTime
        hashMapCart["currentDate"] = saveCurrentDate
        hashMapCart["totalPrice"] = pricePro * numberProduct.text.toString().toInt()

        database.collection("AddToCart").document(auth.currentUser!!.uid)
            .collection("User").add(hashMapCart).addOnCompleteListener {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()

                //update quantities product after buy cart
                val hashMapUpdateNumber: HashMap<String, Any> = HashMap()
                hashMapUpdateNumber["quantities"] = productModel.quantities - numberProduct.text.toString().toInt()
                database.collection("Products")
                    .document(productModel.id).update(hashMapUpdateNumber)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                    }

            }
    }

    private fun setupDataComponent() {
        Glide.with(this).load(imagePro).into(imgProduct)
        nameProduct.text = namePro
        starProduct.text = ratingPro
        descProduct.text = descPro
        priceProduct.text = decimalFormat.format(pricePro).toString().plus(" VNĐ")
        numberProduct.text = numPro.toString()
    }

    private fun getData() {

        val obj = intent.getSerializableExtra("data")
        if (obj is ProductModel) {
            productModel = obj
        }
        quantityProduct = productModel.quantities
        pricePro = productModel.price
        imagePro = productModel.img_url
        namePro = productModel.name
        descPro = productModel.description
        ratingPro = productModel.rating
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        val actionBar: ActionBar? = supportActionBar
        supportActionBar?.title = "Detail product"
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_cart -> {
            }
            R.id.action_notice -> {
            }
            R.id.action_mute -> {
            }
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initComponent() {
        imgProduct = findViewById(R.id.img_product)
        nameProduct = findViewById(R.id.name_product)
        starProduct = findViewById(R.id.star_product)
        descProduct = findViewById(R.id.desc_product)
        priceProduct = findViewById(R.id.price_product)
        numberProduct = findViewById(R.id.number_product)
        btnMinus = findViewById(R.id.btn_minius)
        btnPlus = findViewById(R.id.btn_plus)
        btnAddToCart = findViewById(R.id.btn_add_to_cart)
        btnBuyNow = findViewById(R.id.btn_buy_now)
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
//        val act = (activity)LoginActivity()
        userModel = UserModel()
    }

}