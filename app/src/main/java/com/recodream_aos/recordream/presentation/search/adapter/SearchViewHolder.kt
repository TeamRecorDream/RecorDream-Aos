package com.recodream_aos.recordream.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.recodream_aos.recordream.databinding.ItemRecordFoundBinding

class SearchViewHolder(
    private val binding: ItemRecordFoundBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(i: Int) {
    }

    companion object {
        fun getView(parent: ViewGroup, layoutInflater: LayoutInflater): ItemRecordFoundBinding =
            ItemRecordFoundBinding.inflate(layoutInflater, parent, false)
    }
}
