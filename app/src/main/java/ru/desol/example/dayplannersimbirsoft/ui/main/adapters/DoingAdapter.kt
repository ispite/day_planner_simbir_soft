package ru.desol.example.dayplannersimbirsoft.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.desol.example.dayplannersimbirsoft.data.Doing
import ru.desol.example.dayplannersimbirsoft.databinding.ItemDoingBinding
import ru.desol.example.dayplannersimbirsoft.utils.inflate
import timber.log.Timber

class DoingAdapter(
    private val onDoingClick: (Doing) -> Unit
) : RecyclerView.Adapter<DoingAdapter.DoingViewHolder>() {

    private val doings = mutableListOf<Doing>()

    fun submitList(doings: List<Doing>) {
        this.doings.clear()
        this.doings.addAll(doings)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoingViewHolder {
        return DoingViewHolder(parent.inflate(ItemDoingBinding::inflate), onDoingClick)
    }

    override fun getItemCount(): Int = doings.size

    override fun onBindViewHolder(holder: DoingViewHolder, position: Int) {
        val currentDoing = doings[position]
        holder.bind(currentDoing)
    }


    class DoingViewHolder(
        private val binding: ItemDoingBinding,
        private val onDoingClick: (Doing) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var doing: Doing? = null

        init {
            binding.root.setOnClickListener {
                doing?.let { onDoingClick(it) }
            }
        }

        fun bind(item: Doing) {
            doing = item
            with(binding) {
                titleDoing.text = item.name
                descriptionDoing.text = item.description
            }
        }
    }
}