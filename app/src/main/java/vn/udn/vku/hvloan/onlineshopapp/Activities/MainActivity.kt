package vn.udn.vku.hvloan.onlineshopapp.Activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import vn.udn.vku.hvloan.onlineshopapp.Adapter.CategoryAdapter
import vn.udn.vku.hvloan.onlineshopapp.Adapter.ProductAdapter
import vn.udn.vku.hvloan.onlineshopapp.Adapter.ProductSearchAdapter
import vn.udn.vku.hvloan.onlineshopapp.Adapter.SlideAdapter
import vn.udn.vku.hvloan.onlineshopapp.R
import vn.udn.vku.hvloan.onlineshopapp.Models.CategoryModel
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import java.util.*
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide
import vn.udn.vku.hvloan.onlineshopapp.Models.CartModel
import vn.udn.vku.hvloan.onlineshopapp.Models.UserModel
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    lateinit var slideHome: ViewPager
    lateinit var rcvCategory: RecyclerView
    lateinit var rcvProduct: RecyclerView
    lateinit var btnViewAll: TextView
    lateinit var imageUser: CircleImageView
    private var currentPage = 0
    lateinit var imagesArray: Array<Int>
    lateinit var categoryModel: CategoryModel
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var arrayListCategory: ArrayList<CategoryModel>
    lateinit var productModel: ProductModel
    lateinit var productAdapter: ProductAdapter
    private lateinit var arrayListProduct: ArrayList<ProductModel>
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var searchView: AutoCompleteTextView
    private lateinit var cartModel: CartModel
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponent()
        setupToolbar()
        getDataSlide()
        setupSlide()
        getDataRcvCategory()
        setupRcvCategory()
        getDataRcvProduct()
        setupRcvProduct()
        setupImageUser()
        actionComponent()

    }

    private fun setupImageUser() {
        database.collection("Users").document(auth.currentUser!!.uid).collection("profile")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    userModel = document.toObject(UserModel::class.java)
                    if (userModel.avtImage == "" || userModel.avtImage.isEmpty()) {
                        userModel.avtImage = "https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png"
                    }
                    Glide.with(this).load(userModel.avtImage).into(imageUser)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun actionComponent() {
        btnViewAll.setOnClickListener {
            startActivity(Intent(this, AllProductActivity::class.java))
        }

        imageUser.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupToolbar() {
        val toolbarHome: Toolbar = findViewById(R.id.toolbar_home)
        setSupportActionBar(toolbarHome)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu_toolbar_home, menu)

        val itemSearch = menu!!.findItem(R.id.action_search)
        searchView = itemSearch.actionView as AutoCompleteTextView
        searchView.hint = "What are you finding?"
        searchView.width = 800

        database.collection("AddToCart").document(auth.currentUser!!.uid)
            .collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    cartModel = document.toObject(CartModel::class.java)
                }
                if (cartModel.productName.isNotEmpty()) {
                    val iconCart = menu.getItem(1)
                    iconCart.setIcon(R.drawable.shopping_cart_order)
                } else {

                }
            }
            .addOnFailureListener {
            }


        return true
    }

    private fun setupProductSearchAdapter(listProduct: MutableList<ProductModel>) {
        val productSearchAdapter = ProductSearchAdapter(
            this,
            R.layout.item_search,
            listProduct as ArrayList<ProductModel>
        )

        searchView.setAdapter(productSearchAdapter)

        // Sau khi chọn item search sẽ chuyển sang fragment detail
        searchView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("data", listProduct[position])
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_exit-> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
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

    private fun setupRcvProduct() {
        productAdapter = ProductAdapter(this, arrayListProduct)
        rcvProduct.layoutManager = GridLayoutManager(this,2)
        rcvProduct.adapter = productAdapter

        productAdapter.onItemClick = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("data", it)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvProduct() {
        database.collection("Products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productModel = document.toObject(ProductModel::class.java)
                    productModel.id = document.id
                    arrayListProduct.add(productModel)
                    productAdapter.notifyDataSetChanged()
                }
                setupProductSearchAdapter(arrayListProduct)
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun setupRcvCategory() {
        categoryAdapter = CategoryAdapter(this, arrayListCategory)
        rcvCategory.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rcvCategory.adapter = categoryAdapter

        categoryAdapter.onItemClick = {
            val intent = Intent(this, AllProductActivity::class.java)
            intent.putExtra("dataType", it.type)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDataRcvCategory() {
        database.collection("Categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    categoryModel = document.toObject(CategoryModel::class.java)
                    arrayListCategory.add(categoryModel)
                    categoryAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initComponent() {
        slideHome = findViewById(R.id.slide_home)
        rcvCategory = findViewById(R.id.rcv_category)
        imageUser = findViewById(R.id.img_avt_home)
        database = FirebaseFirestore.getInstance()
        arrayListCategory = arrayListOf<CategoryModel>()
        rcvProduct = findViewById(R.id.rcv_item)
        arrayListProduct = arrayListOf()
        btnViewAll = findViewById(R.id.btn_see_all)
        auth = FirebaseAuth.getInstance()

        cartModel = CartModel()
        userModel = UserModel()
    }

    private fun setupSlide() {
        val adapterSlide: PagerAdapter = SlideAdapter(this, imagesArray)
        slideHome.adapter = adapterSlide

        val handler = Handler()
        val update = Runnable {
            if (currentPage == imagesArray.size) {
                currentPage = 0
            }
            slideHome.setCurrentItem(currentPage++, true)
        }

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        },500, 3000)
    }

    private fun getDataSlide() {
        imagesArray = arrayOf(
            R.drawable.slide1,
            R.drawable.slide2,
            R.drawable.slide3,
            R.drawable.slide4
        )
    }


}