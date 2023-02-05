
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bonsaicare.R

typealias OnImageClick = (position: Int) -> Unit

class ImageGalleryAdapter(private val mList: MutableList<Uri>) : RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {
    var onImageClick: OnImageClick? = null

    // Create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflates the card_view_design view that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_image_view_for_gallery, parent, false)

        return ViewHolder(view)
    }

    // Bind the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = mList[position]

        // Set the images and texts for the tree list to the imageview from itemHolder class
        holder.imageViewGallery.setImageURI(image)

        holder.imageViewGallery.setOnClickListener{
            // Swap clicked image to number one
            onImageClick?.invoke(position)
        }
    }

    // Return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val imageViewGallery: ImageView = itemView.findViewById(R.id.imageView_tree_detail_image_gallery)
    }
}