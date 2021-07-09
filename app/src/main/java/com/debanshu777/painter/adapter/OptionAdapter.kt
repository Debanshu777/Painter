package com.debanshu777.painter.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.debanshu777.painter.R
import com.debanshu777.painter.model.Option
import kotlinx.android.synthetic.main.item_option.view.*

class OptionAdapter(private val list: ArrayList<Option>) : RecyclerView.Adapter<OptionAdapter.OptionsViewHolder>() {
    class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.optionName
        val imageView: ImageButton = itemView.optionIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        return OptionsViewHolder(itemView)
    }

    private var onItemClickListener: ((Option) -> Unit)? = null

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val currentItem = list[position]
        holder.textView.text = currentItem.optionName
        holder.imageView.setImageResource(currentItem.icon)
        holder.itemView.apply {
            holder.textView.setTypeface(holder.textView.typeface,Typeface.BOLD)
            setOnClickListener {
                onItemClickListener?.let { it(currentItem) }
            }
        }
    }

    override fun getItemCount() = list.size

    fun setOnItemClickListener(listener: (Option) -> Unit) {
        onItemClickListener = listener
    }
}
