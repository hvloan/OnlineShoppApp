package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import vn.udn.vku.hvloan.onlineshopapp.Models.ProductModel
import vn.udn.vku.hvloan.onlineshopapp.R
import java.text.DecimalFormat

class ProductSearchAdapter(context: Context, resource: Int, arrayListSearch: ArrayList<ProductModel>) : ArrayAdapter<ProductModel>(context, resource, arrayListSearch), Filterable {

    private val formatPrice: DecimalFormat = DecimalFormat("###,###,###")
    private val listSearchProduct: ArrayList<ProductModel> = ArrayList(arrayListSearch)

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertview = convertView
        if (convertview == null) {
            convertview = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        }
        val imgSearch = convertview!!.findViewById<ImageView>(R.id.img_search)
        val tvSearchName = convertview.findViewById<TextView>(R.id.tv_search_name)
        val tvSearchPrice = convertview.findViewById<TextView>(R.id.tv_search_price)
        val product = getItem(position)
        Glide.with(context).load(product!!.img_url).into(imgSearch)
        tvSearchName?.text = product.name
        tvSearchPrice?.text = formatPrice.format(product.price.toLong()) + " VNĐ"
        return convertview
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val listSuggest: ArrayList<ProductModel> = ArrayList()
                if (constraint == null || constraint.isEmpty()) {
                    listSuggest.addAll(listSearchProduct)
                } else {
                    val filter = constraint.toString().toLowerCase().trim { it <= ' ' }
                    for (p in listSearchProduct) {
                        if (p.name.toLowerCase().contains(filter)) {
                            listSuggest.add(p)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = listSuggest
                filterResults.count = listSuggest.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                addAll(results?.values as ArrayList<ProductModel>)
                notifyDataSetInvalidated()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as ProductModel).name
            }
        }
    }

}