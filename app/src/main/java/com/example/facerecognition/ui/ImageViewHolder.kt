package com.example.facerecognition.ui

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.facerecognition.R
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun inflate(parent: ViewGroup): ImageViewHolder =
            ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_image, parent, false)
            )
    }

    fun bind(fileName: String) {
        val imageDir = itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filePath = "$imageDir/$fileName.png"
        Glide.with(itemView.context)
            .load(File(filePath))
            .error(R.drawable.ic_broken_image)
            .fallback(R.drawable.ic_broken_image)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .dontAnimate()
            .into(itemView.ivImage)
    }

    fun setActionListener(listener: (position: Int) -> Unit) {
        itemView.setOnClickListener {
            listener.invoke(adapterPosition)
        }
    }
}
