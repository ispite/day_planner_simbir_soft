package ru.desol.example.dayplannersimbirsoft.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.desol.example.dayplannersimbirsoft.data.Doing
import ru.desol.example.dayplannersimbirsoft.databinding.ItemDoingBinding
import ru.desol.example.dayplannersimbirsoft.utils.inflate

class DoingAdapter : RecyclerView.Adapter<DoingAdapter.DoingViewHolder>() {

    val doings = mutableListOf<Doing>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoingViewHolder {
        return DoingViewHolder(parent.inflate(ItemDoingBinding::inflate))
    }

    override fun getItemCount(): Int = doings.size

    override fun onBindViewHolder(holder: DoingViewHolder, position: Int) {
        val currentDoing = doings[position]
        holder.bind(currentDoing)
    }


    class DoingViewHolder(private val binding: ItemDoingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Doing) {
            with(binding) {
                titleDoing.text = item.name
                descriptionDoing.text = item.description
            }
        }
    }
}