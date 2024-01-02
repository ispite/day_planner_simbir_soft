package ru.desol.example.dayplannersimbirsoft.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.desol.example.dayplannersimbirsoft.data.Hour
import ru.desol.example.dayplannersimbirsoft.databinding.ItemOneHourBinding
import ru.desol.example.dayplannersimbirsoft.utils.inflate

class HoursAdapter : RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {

    private var hourList: List<Hour> = emptyList()

    fun submitList(hourList: List<Hour>) {
        this.hourList = hourList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(parent.inflate(ItemOneHourBinding::inflate))
    }

    override fun getItemCount(): Int = hourList.size

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val currentHour = hourList[position]
        holder.bind(currentHour)
    }

    class HourViewHolder(private val binding: ItemOneHourBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hour) {
            with(binding) {
                hourTextView.text = item.id.toString()
                titleTextView.text = item.title
            }
        }
    }
}