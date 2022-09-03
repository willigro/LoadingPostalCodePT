package com.rittmann.common.components

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class DataBindingBaseAdapter<T>(@NonNull private val values: ArrayList<T>) :
    RecyclerView.Adapter<DataBindingBaseAdapter.DataBindingBaseViewHolder<T>>() {

    protected var itemClickListener: ((View, Int, item: T) -> Unit)? = null
    protected lateinit var rootRecyclerView: RecyclerView

    interface OnItemClickListener {
        fun onItemClicked(view: View, positions: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBindingBaseViewHolder<T> =
        createViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                getItemLayout(), parent, false
            )
        )

    override fun onBindViewHolder(holder: DataBindingBaseViewHolder<T>, position: Int) {
        if (values.isEmpty().not()) {
            holder.apply {
                bind(values[position], position, itemClickListener)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rootRecyclerView = recyclerView
    }

    fun subscribeToItemClick(listener: (view: View, position: Int, item: T) -> Unit) {
        itemClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun swapData(list: List<T>) {
        values.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: T) {
        values.add(item)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: T) {
        values.remove(item)

        notifyDataSetChanged()
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getItem(position: Int): T = values[position]

    override fun getItemCount(): Int {
        return if (values.isNullOrEmpty()) 0 else values.size
    }

    abstract fun createViewHolder(viewBinding: ViewDataBinding): DataBindingBaseViewHolder<T>

    abstract fun getItemLayout(): Int

    abstract class DataBindingBaseViewHolder<T>(viewBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        abstract fun bind(
            item: T,
            position: Int,
            itemClickListener: ((View, Int, T) -> Unit)? = null
        )
    }
}