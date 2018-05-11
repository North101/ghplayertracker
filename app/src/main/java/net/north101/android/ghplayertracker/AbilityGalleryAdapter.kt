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
import net.north101.android.ghplayertracker.data.Ability
import net.north101.android.ghplayertracker.data.CharacterClass

class AbilityGalleryAdapter : RecyclerView.Adapter<BaseViewHolder<*>>() {
    val items = ArrayList<RecyclerItemCompare>()
    var onImageItemClick: ((Ability) -> Unit)? = null

    data class ClassAbility(
        val classId: String,
        val ability: Ability?
    ) : RecyclerItemCompare {
        override val compareItemId: String
            get() = ability?.compareItemId ?: ""
    }

    fun updateItems(characterClass: CharacterClass, columns: Int) {
        val newItems = ArrayList<RecyclerItemCompare>()
        val headers = ArrayList<TextHeader>()
        val abilities = ArrayList<ClassAbility>()
        var level = 0
        var special: String? = null
        for (item in characterClass.abilities) {
            if (level != item.level || special != item.special) {
                if (level == 1 || columns == 2 || headers.size == 2) {
                    newItems.addAll(headers)
                    headers.clear()
                    for (index in 0 until (columns - abilities.size % columns)) {
                        abilities.add(ClassAbility(characterClass.id, null))
                    }
                    newItems.addAll(abilities)
                    abilities.clear()
                }
                headers.add(TextHeader("Level ${item.level}${item.special}"))
                level = item.level
                special = item.special
            }

            abilities.add(ClassAbility(characterClass.id, item))
        }
        newItems.addAll(headers)
        newItems.addAll(abilities)

        val diffCallback = RecyclerListItemsCallback(this.items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (GalleryViewType.values()[viewType]) {
            GalleryViewType.Header -> TextHeaderViewHolder.inflate(parent)
            GalleryViewType.AbilityImage -> AbilityImageViewHolder.inflate(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val item = items[position]

        when (holder) {
            is TextHeaderViewHolder -> holder.bind(item as TextHeader)
            is AbilityImageViewHolder -> {
                holder.bind(item as ClassAbility)
                holder.onItemClick = { ability ->
                    onImageItemClick?.invoke(ability)
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
            is ClassAbility -> GalleryViewType.AbilityImage
            else -> throw RuntimeException(position.toString())
        }.ordinal
    }
}

class AbilityImageViewHolder(itemView: View) : BaseViewHolder<AbilityGalleryAdapter.ClassAbility>(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)

    var onItemClick: ((Ability) -> Unit)? = null

    init {
        imageView.setOnClickListener {
            item!!.ability?.let { onItemClick?.invoke(it) }
        }
    }

    override fun bind(item: AbilityGalleryAdapter.ClassAbility) {
        super.bind(item)

        item.ability?.let {
            // Set transition name same as the AbilityImage name
            ViewCompat.setTransitionName(imageView, it.id)

            Glide.with(itemView.context)
                .load(it.url)
                .apply(
                    RequestOptions()
                        .placeholder(Util.getImageResource(itemView.context, CharacterClass.cardBack(item.classId)))
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .into(imageView)
        }
    }

    override fun unbind() {
        // Set transition name same as the AbilityImage name
        ViewCompat.setTransitionName(itemView, null)
        imageView.setImageResource(0)

        super.unbind()
    }

    companion object {
        var layout = R.layout.ability_gallery_item

        fun inflate(parent: ViewGroup): AbilityImageViewHolder {
            return AbilityImageViewHolder(BaseViewHolder.inflate(parent, layout))
        }
    }
}

enum class GalleryViewType {
    Header,
    AbilityImage
}