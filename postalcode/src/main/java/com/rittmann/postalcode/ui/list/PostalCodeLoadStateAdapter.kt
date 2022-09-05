package com.rittmann.postalcode.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rittmann.postalcode.R

class PostalCodeLoadStateAdapter : LoadStateAdapter<PostalCodeLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val progress = view.findViewById<View>(R.id.progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.postal_code_footer_loading, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.progress.isVisible = loadState is LoadState.Loading
    }
}
