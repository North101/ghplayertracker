package net.north101.android.ghplayertracker

import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.*

class GalleryAdapter(
    private val galleryList: ArrayList<ImageModel>,
    private val galleryItemClickListener: GalleryItemClickListener
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item, parent, false))
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val imageModel = galleryList[position]

        Glide.with(holder.galleryImageView.context)
            .load(imageModel.url)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.galleryImageView)

        // Set transition name same as the Image name
        ViewCompat.setTransitionName(holder.galleryImageView, imageModel.name)

        holder.galleryImageView.setOnClickListener {
            galleryItemClickListener.onGalleryItemClickListener(holder.adapterPosition, imageModel, holder.galleryImageView)
        }
    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val galleryImageView: ImageView = view.findViewById(R.id.galleryImage)
    }
}