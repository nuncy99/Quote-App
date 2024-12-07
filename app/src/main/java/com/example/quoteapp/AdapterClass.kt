package com.example.quoteapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(private val dataList: MutableList<Quote>,
private val onClick: (Quote?) ->Unit): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterClass.ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout,parent,false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: AdapterClass.ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.quotes.text = currentItem.quote
        holder.itemView.setOnClickListener {
            onClick(currentItem)
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val quotes :TextView = itemView.findViewById(R.id.qouteTextView)

    }


}
