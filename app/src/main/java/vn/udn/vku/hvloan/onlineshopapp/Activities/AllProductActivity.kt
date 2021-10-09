package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Adapter.AllProductAdapter
import vn.udn.vku.hvloan.onlineshopapp.Models.CategoryModel
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.util.*

class AllProductActivity : AppCompatActivity() {

    private lateinit var rcvAllProduct: RecyclerView
    lateinit var productModel: ProductModel
    lateinit var categoryModel: CategoryModel
    lateinit var allProductAdapter: AllProductAdapter
    private lateinit var arrayListProduct: ArrayList<ProductModel>
    private lateinit var database: FirebaseFirestore

    private lateinit var typeCategory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_product)

        initComponent()
        setupToolbar()
        getDataRcvAllProduct()
        setupRcvAllProduct()


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvAllProduct() {
        if (typeCategory.isEmpty() || typeCategory == "" || typeCategory == "null") {
            supportActionBar?.title = "All products"
            database.collection("Products")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        productModel = document.toObject(ProductModel::class.java)
                        productModel.id = document.id
                        arrayListProduct.add(productModel)
                        allProductAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        } else {
            supportActionBar?.title = typeCategory.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            database.collection("Products").whereEqualTo("type", typeCategory)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        productModel = document.toObject(ProductModel::class.java)
                        productModel.id = document.id
                        arrayListProduct.add(productModel)
                        allProductAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }

    }

    private fun setupRcvAllProduct() {
        allProductAdapter = AllProductAdapter(this, arrayListProduct)
        rcvAllProduct.layoutManager = GridLayoutManager(this,2)
        rcvAllProduct.adapter = allProductAdapter

        allProductAdapter.onItemClick = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("data", it)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar_all_pro))

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setDisplayShowHomeEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun initComponent() {
        rcvAllProduct = findViewById(R.id.rcv_all_pro)
        database = FirebaseFirestore.getInstance()
        arrayListProduct = arrayListOf()

        if (intent != null) {
            typeCategory = intent.extras?.getString("dataType").toString()
        } else {
            typeCategory = ""
        }
    }
}