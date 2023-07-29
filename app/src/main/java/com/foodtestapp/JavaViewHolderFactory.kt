package com.foodtestapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView


object JavaViewHolderFactory {

    fun create(view: View, viewType: Int, listener: OnItemClickListener,
                overlaySelectionListener: OnOverlaySelectionListener? = null): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_subcategory -> SubcatHolder(view, listener, overlaySelectionListener)

            else -> GenericViewHolder(view)
        }
    }
}