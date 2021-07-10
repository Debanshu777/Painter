package com.debanshu777.painter.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.debanshu777.painter.R
import kotlinx.android.synthetic.main.item_gallery.view.*
import java.io.File

class GalleryAdapter (private val list: ArrayList<File>) : RecyclerView.Adapter<GalleryAdapter.OptionsViewHolder>() {
    class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return OptionsViewHolder(itemView)
    }

    private var onItemClickListener: ((File) -> Unit)? = null

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val currentItem = list[position]
        holder.imageView.setImageURI(Uri.fromFile(currentItem))
        holder.imageView.apply {
            setOnClickListener {
                onItemClickListener?.let { it(currentItem) }
            }
        }
    }

    override fun getItemCount() = list.size

    fun setOnItemClickListener(listener: (File) -> Unit) {
        onItemClickListener = listener
    }
}
