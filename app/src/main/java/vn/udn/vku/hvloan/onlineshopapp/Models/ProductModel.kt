package vn.udn.vku.hvloan.onlineshopapp.Models

import java.io.Serializable

class ProductModel(
    val img_url: String = "",
    val description: String = "",
    val name: String = "",
    val rating: String = "",
    val price: Int = 0,
    var id: String = "",
    var quantities: Int = 0,
    val type: String = ""): Serializable {
}