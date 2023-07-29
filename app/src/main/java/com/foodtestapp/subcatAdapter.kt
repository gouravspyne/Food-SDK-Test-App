package com.foodtestapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SubcatAdapter (
    list: List<Any>,
    var listener: OnItemClickListener,
    var overlaySelectionListener : OnOverlaySelectionListener
) : GenericAdapter<Any>(list) {

    override fun getLayoutId(position: Int, obj: Any?): Int {
        return when (obj) {
            is SubcatData -> R.layout.item_subcategory
            else -> error("Unknown type: for position: $position")
        }
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return JavaViewHolderFactory.create(view, viewType, listener,overlaySelectionListener)
    }
}