
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bonsaicare.R
import androidx.recyclerview.widget.ListAdapter
import com.example.bonsaicare.ui.calendar.BonsaiViewModel
import com.example.bonsaicare.ui.mygarden.CardDatabaseTree
import java.util.*

class TreeListAdapter(private val sharedBonsaiViewModel: BonsaiViewModel) : ListAdapter<CardDatabaseTree, TreeListAdapter.CardViewHolder>(CARDS_COMPARATOR) {

    private lateinit var context: Context

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.textView_tree_list_name)
        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.imageView_tree_list_thumbnail_image)
        private val treeSpeciesNameTextView: TextView = itemView.findViewById(R.id.textView_list_tree_species_name)
        private val ageTextView: TextView = itemView.findViewById(R.id.textView_tree_list_age)

        // Get reference to linearLayoutCalendar
        val linearLayoutTreeList: LinearLayout = itemView.findViewById(R.id.linear_layout_tree_list)

        fun bind(name: String?, thumbnailImage: Uri, treeSpeciesName: String?, age: Int?) {

            // Set texts for textViews
            nameTextView.text = name
            thumbnailImageView.setImageURI(thumbnailImage)
            treeSpeciesNameTextView.text = treeSpeciesName
            ageTextView.text = "Age: " + age.toString()
        }

        companion object {
            fun create(parent: ViewGroup): CardViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tree_list_card_view, parent, false)
                return CardViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        context = parent.context

        // Create a new view
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.tree_list_card_view, parent, false)

        return CardViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        // Get current Item by position
        val currentCard = getItem(position)

        // Set on click listener to navigate to tree details
        holder.linearLayoutTreeList.setOnClickListener {
                view : View ->
            sharedBonsaiViewModel.currentTreeIndex = position
            view.findNavController().navigate(R.id.action_navigation_my_garden_to_tree_detail_card)
        }

        // If thumbnail image is not set, set default image R.drawable.trees_icon_colored
        if (currentCard.thumbnailImage == Uri.EMPTY) {
            currentCard.thumbnailImage = Uri.parse("android.resource://com.example.bonsaicare_v3/drawable/trees_icon_colored")
        }

        // Bind information to holder
        holder.bind(currentCard.name, currentCard.thumbnailImage , currentCard.treeSpecies.name, currentCard.age)
    }

    companion object {
        private val CARDS_COMPARATOR = object : DiffUtil.ItemCallback<CardDatabaseTree>() {
            override fun areItemsTheSame(oldItem: CardDatabaseTree, newItem: CardDatabaseTree): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: CardDatabaseTree, newItem: CardDatabaseTree): Boolean {
                return oldItem.cardName == newItem.cardName
            }
        }
    }
}