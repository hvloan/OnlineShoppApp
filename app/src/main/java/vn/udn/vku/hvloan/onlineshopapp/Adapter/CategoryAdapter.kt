package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.udn.vku.hvloan.onlineshopapp.Models.CategoryModel
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R

class CategoryAdapter(val context: Context?, private val categoryList: ArrayList<CategoryModel>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var onItemClick: ((CategoryModel) -> Unit)? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var nameCategory: TextView = view.findViewById<TextView>(R.id.tv_name_category)
        var imgCategory: ImageView = view.findViewById<ImageView>(R.id.img_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameCategory.text = categoryList[position].name
        Glide.with(context!!).load(categoryList[position].img_url).into(holder.imgCategory)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(categoryList[position])
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}

