package com.app.digitifysample.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.digitifysample.common.callbacks.RecyclerViewCallback
import com.app.digitifysample.common.callbacks.RecyclerViewItemCallback
import com.app.digitifysample.databinding.ConversionRateListitemBinding
import com.app.digitifysample.datasource.models.ConversionRates
import com.app.digitifysample.home.vm.ConversionRateItemViewModel

class ConversionRateListAdapter(
    private val callback: RecyclerViewCallback<ConversionRates>
) :
    PagingDataAdapter<ConversionRates, ConversionRateListAdapter.ConversionRateViewHolder>(
        EventComparator
    ) {

    override fun onBindViewHolder(holder: ConversionRateViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionRateViewHolder {
        return ConversionRateViewHolder(
            ConversionRateListitemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ConversionRateViewHolder(binding: ConversionRateListitemBinding) :
        RecyclerView.ViewHolder(binding.root), RecyclerViewItemCallback<ConversionRates> {

        private val vm = ConversionRateItemViewModel(binding.root.context)

        init {
            binding.cb = this
            binding.vm = vm
            binding.executePendingBindings()
        }

        fun bind(rate: ConversionRates) {
            vm.setData(rate)
        }

        override fun onListItemClicked(item: ConversionRates) {
            callback.onListItemClicked(item, absoluteAdapterPosition)
        }
    }

    object EventComparator : DiffUtil.ItemCallback<ConversionRates>() {
        override fun areItemsTheSame(oldItem: ConversionRates, newItem: ConversionRates): Boolean {
            // Id is unique.
            return oldItem.sourceCurrency == newItem.sourceCurrency
        }

        override fun areContentsTheSame(
            oldItem: ConversionRates,
            newItem: ConversionRates
        ): Boolean {
            return oldItem == newItem
        }
    }

}