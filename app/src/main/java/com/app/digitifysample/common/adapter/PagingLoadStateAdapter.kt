package com.app.digitifysample.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.digitifysample.databinding.PagingFooterItemBinding


class PagingLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PagingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        val progress = holder.binding.pbProgress
        val btnRetry = holder.binding.bRetry

        btnRetry.isVisible = loadState !is LoadState.Loading
        progress.isVisible = loadState is LoadState.Loading

        btnRetry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            PagingFooterItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class LoadStateViewHolder(val binding: PagingFooterItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}