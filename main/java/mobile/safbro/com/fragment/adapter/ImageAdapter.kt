package mobile.safbro.com.fragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import mobile.safbro.com.R

class ImageAdapter(
    private val imageList: List<String>, // List to store image URLs
    private val titleList: List<String> // List to store titles
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    // Define an interface for item click events
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onPlayPauseButtonClick(position: Int)
    }

    // Method to set the item click listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageList[position]
        val title = titleList[position] // Get title from the titleList

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageView)

        // Set the title text
        holder.titleTextView.text = title

        // Set a click listener for the item view
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
