package net.north101.android.ghplayertracker

import android.support.v4.view.ViewCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class AbilityGalleryAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    val items = ArrayList<RecyclerItemCompare>()
    var onImageItemClick: ((ImageUrl) -> Unit)? = null

    fun updateItems(imageList: ArrayList<ImageUrl>) {
        val newItems = ArrayList<RecyclerItemCompare>()
        newItems.addAll(imageList)

        val diffCallback = RecyclerListItemsCallback(this.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (GalleryViewType.values()[viewType]) {
            GalleryViewType.Header -> TextHeaderViewHolder.inflate(parent)
            GalleryViewType.Image -> AbilityImageViewHolder.inflate(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]

        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is AbilityImageViewHolder -> {
                holder.bind(item as ImageUrl)
                holder.onItemClick = { it ->
                    onImageItemClick?.invoke(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return when (item) {
            is TextHeader -> GalleryViewType.Header
            is ImageUrl -> GalleryViewType.Image
            else -> throw RuntimeException(position.toString())
        }.ordinal
    }
}

class AbilityImageViewHolder(itemView: View) : BaseViewHolder<ImageUrl>(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)

    var onItemClick: ((ImageUrl) -> Unit)? = null

    init {
        imageView.setOnClickListener {
            item!!.let { onItemClick?.invoke(it) }
        }
    }

    override fun bind(item: ImageUrl) {
        super.bind(item)

        // Set transition name same as the Image name
        ViewCompat.setTransitionName(imageView, item.imageUrl)

        Glide.with(itemView.context)
            .load(item.imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(Util.getImageResource(itemView.context, item.imagePlaceholder))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .into(imageView)
    }

    override fun unbind() {
        // Set transition name same as the Image name
        ViewCompat.setTransitionName(itemView, null)
        imageView.setImageResource(0)

        super.unbind()
    }

    companion object {
        var layout = R.layout.image_gallery_item

        fun inflate(parent: ViewGroup): AbilityImageViewHolder {
            return AbilityImageViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}

enum class GalleryViewType {
    Header,
    Image
}