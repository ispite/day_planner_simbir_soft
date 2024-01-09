package ru.desol.example.dayplannersimbirsoft.ui.main.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.desol.example.dayplannersimbirsoft.data.CalendarItem
import ru.desol.example.dayplannersimbirsoft.databinding.ItemCalendarBinding
import ru.desol.example.dayplannersimbirsoft.utils.inflate

class CalendarItemAdapter(private val onCalendarItemClick: (id: Long) -> Unit) :
    RecyclerView.Adapter<CalendarItemAdapter.CalendarItemViewHolder>() {

    private var calendarItemList: MutableList<CalendarItem> = mutableListOf()

    fun clearList() {
        calendarItemList.clear()
        notifyDataSetChanged()
    }

    fun addItemToList(calendarItem: CalendarItem) {
        calendarItemList.add(calendarItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemViewHolder {
        return CalendarItemViewHolder(
            parent.inflate(ItemCalendarBinding::inflate),
            onCalendarItemClick
        )
    }

    override fun getItemCount(): Int = calendarItemList.size

    override fun onBindViewHolder(holder: CalendarItemViewHolder, position: Int) {
        val currentCalendarItem = calendarItemList[position]
        holder.bind(currentCalendarItem)
    }

    class CalendarItemViewHolder(
        private val binding: ItemCalendarBinding,
        onCalendarItemClick: (id: Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentCalendarItemId: Long? = null

        init {
            binding.root.setOnClickListener {
                currentCalendarItemId?.let(onCalendarItemClick)
            }
        }

        fun bind(item: CalendarItem) {
            currentCalendarItemId = item.id

            with(binding) {
                item.color?.let {
                    viewColor.setBackgroundColor(it)
                }
                textDisplayName.text = item.displayName
                textAccountName.text = item.accountName
                textAccountType.text = "(${item.accountType})"
                textVisible.text = item.visible.toString()
                textSyncEvents.text = item.syncEvents.toString()
            }
        }
    }
}