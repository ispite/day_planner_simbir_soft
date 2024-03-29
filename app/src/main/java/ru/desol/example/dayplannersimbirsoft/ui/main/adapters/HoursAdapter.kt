package ru.desol.example.dayplannersimbirsoft.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.desol.example.dayplannersimbirsoft.data.Doing
import ru.desol.example.dayplannersimbirsoft.data.Hour
import ru.desol.example.dayplannersimbirsoft.databinding.ItemOneHourBinding
import ru.desol.example.dayplannersimbirsoft.utils.inflate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HoursAdapter(
    private val onDoingClick: (Doing) -> Unit
) : RecyclerView.Adapter<HoursAdapter.HourViewHolder>() {

    private val fullDay: MutableList<Hour> = mutableListOf()

    fun clearList() {
        fullDay.clear()
        notifyDataSetChanged()
    }

    fun submitList(hourList: List<Hour>) {
        this.fullDay.clear()
        this.fullDay.addAll(hourList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(parent.inflate(ItemOneHourBinding::inflate), onDoingClick)
    }

    override fun getItemCount(): Int = fullDay.size

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val currentHour = fullDay[position]
        holder.bind(currentHour)
    }

    class HourViewHolder(
        private val binding: ItemOneHourBinding,
        private val onDoingClick: (Doing) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Hour) {
            val doingAdapter = DoingAdapter() { onDoingClick(it) }
            doingAdapter.submitList(item.doingsInHour ?: emptyList())

            with(binding) {
                hourTextView.text = getTimeInterval(item.id)
                doingRecyclerView.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                doingRecyclerView.adapter = doingAdapter
            }
        }

        private fun getTimeInterval(itemId: Int): String {
            val formatter = DateTimeFormatter.ofPattern("HH.mm")
            val hour = LocalTime.of(itemId, 0).format(formatter)
            val nextId = if (itemId < 23) itemId + 1 else 0
            val nextHour = LocalTime.of(nextId, 0).format(formatter)
            return "$hour-$nextHour"
        }
    }
}