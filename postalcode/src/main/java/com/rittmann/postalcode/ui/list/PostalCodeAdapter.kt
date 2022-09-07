package com.rittmann.postalcode.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rittmann.common.model.PostalCode
import com.rittmann.postalcode.R

// Maybe i'll change it for binding
class PostalCodeAdapter :
    PagingDataAdapter<PostalCode, PostalCodeAdapter.PostalCodeViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostalCode>() {
            override fun areItemsTheSame(oldItem: PostalCode, newItem: PostalCode): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PostalCode, newItem: PostalCode): Boolean =
                oldItem == newItem
        }
    }

    inner class PostalCodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNumber = view.findViewById<TextView>(R.id.adapter_postal_code_number)
        val textName = view.findViewById<TextView>(R.id.adapter_postal_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostalCodeViewHolder {
        return PostalCodeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_postal_code, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostalCodeViewHolder, position: Int) {
        val item = getItem(position)
        item?.also {
            with(holder) {
                textNumber.text = textNumber.context.getString(
                    R.string.postal_code_adapter_postal_number,
                    item.numCodPostal.toString(),
                    item.extCodPostal.toString()
                )

                textName.text = item.nameLocalidade
            }
        }
    }
}
