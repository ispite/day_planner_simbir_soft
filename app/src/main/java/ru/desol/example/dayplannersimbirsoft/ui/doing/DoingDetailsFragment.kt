package ru.desol.example.dayplannersimbirsoft.ui.doing


import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.desol.example.dayplannersimbirsoft.R
import ru.desol.example.dayplannersimbirsoft.data.Doing
import ru.desol.example.dayplannersimbirsoft.databinding.FragmentDoingBinding
import java.sql.Timestamp


class DoingDetailsFragment : Fragment(R.layout.fragment_doing) {

    private val binding: FragmentDoingBinding by viewBinding(CreateMethod.INFLATE)
    private val args: DoingDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val doing = args.doing
        setupViews(doing)

        return binding.root
    }

    private fun setupViews(doing: Doing) {
        with(binding) {
            titleDoing.text = doing.name
            descriptionDoing.text = doing.description

            dateTextView.text = getDate(doing.dateStart, doing.dateFinish)
        }
    }

    private fun getDate(startTime: Timestamp, endTime: Timestamp): String {
        val resultStart = DateFormat.format("HH:mm", startTime).toString()
        val resultEnd = DateFormat.format("HH:mm yyyy-MM-dd", endTime).toString()

        return "$resultStart - $resultEnd"
    }
}