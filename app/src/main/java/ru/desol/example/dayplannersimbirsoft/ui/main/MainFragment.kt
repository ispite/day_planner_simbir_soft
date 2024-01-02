package ru.desol.example.dayplannersimbirsoft.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.desol.example.dayplannersimbirsoft.R
import ru.desol.example.dayplannersimbirsoft.data.Hour
import ru.desol.example.dayplannersimbirsoft.databinding.FragmentMainBinding
import ru.desol.example.dayplannersimbirsoft.ui.main.adapters.HoursAdapter

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(CreateMethod.INFLATE)

    // Todo lateinit или nullable?
//    private var hoursAdapter: HoursAdapter? = null
    private lateinit var hoursAdapter: HoursAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupRecyclerView()

        // MOCK
        mockHours()
        hoursAdapter.submitList(hoursList)

        return binding.root
    }

    private fun setupRecyclerView() {
        hoursAdapter = HoursAdapter()
        with(binding.todoRecyclerView) {
            adapter = hoursAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private val hoursList: MutableList<Hour> = mutableListOf()
    private fun mockHours() {
        for (i in 1..12) {
            hoursList.add(Hour(i, "Title: $i"))
        }
    }
}