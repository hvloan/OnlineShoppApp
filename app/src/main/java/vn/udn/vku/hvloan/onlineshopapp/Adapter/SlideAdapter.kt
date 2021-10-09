package vn.udn.vku.hvloan.onlineshopapp.Adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.widget.ImageView
import vn.udn.vku.hvloan.onlineshopapp.Activities.MainActivity
import vn.udn.vku.hvloan.onlineshopapp.R

class SlideAdapter(private val activity: MainActivity, private val imagesArray: Array<Int>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = activity.layoutInflater
        val viewItem: View = inflater.inflate(R.layout.item_slide, container, false)
        val imageView = viewItem.findViewById<ImageView>(R.id.img_slide)
        imageView.setImageResource(imagesArray[position])
        (container as ViewPager).addView(viewItem)
        return viewItem
    }

    override fun getCount(): Int {
        // TODO Auto-generated method stub
        return imagesArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        // TODO Auto-generated method stub
        return view === `object` as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // TODO Auto-generated method stub
        (container as ViewPager).removeView(`object` as View)
    }
}