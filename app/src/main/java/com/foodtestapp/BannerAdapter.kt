package com.foodtestapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BannerAdapter(
    val context: Context,
private val items: List<Drawable>
) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cvBanner: CardView = itemView.findViewById(R.id.cvBanner)
        private val ivBanner: ImageView = itemView.findViewById(R.id.ivBanner)

        fun bind(item: Drawable) {

            cvBanner.cardElevation = 0F

            val drawableResourceId = item

            Glide.with(BaseApplication.getContext())
                .load(drawableResourceId)
                .into(ivBanner)

        }
    }
}