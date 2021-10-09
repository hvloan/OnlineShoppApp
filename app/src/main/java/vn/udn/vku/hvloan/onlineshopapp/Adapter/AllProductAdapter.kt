package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat

class AllProductAdapter(val context: Context?, val productList: ArrayList<ProductModel>): RecyclerView.Adapter<AllProductAdapter.ViewHolder>() {

    var onItemClick: ((ProductModel) -> Unit)? = null

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.img_item)
        val nameProduct: TextView = view.findViewById<TextView>(R.id.tv_name_item)
        val priceProduct: TextView = view.findViewById<TextView>(R.id.tv_price_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_all_product,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val decimalFormat = DecimalFormat("###,###,###")
        holder.nameProduct.text = productList[position].name
        holder.priceProduct.text = decimalFormat.format(productList[position].price).toString().plus(" VNĐ")
        Glide.with(context!!).load(productList[position].img_url).into(holder.imgProduct)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}