package com.example.edistynytandroidkurssi

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.edistynytandroidkurssi.databinding.FragmentCalendarBinding
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root






        //Initialize calendar with date
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())



        //Show Monday as first date of week
        binding.calendarView.setFirstDayOfWeek(Calendar.MONDAY)



        //Show/hide overflow days of a month
        binding.calendarView.setShowOverflowDate(false)



        //call refreshCalendar to update calendar the view
        binding.calendarView.refreshCalendar(currentCalendar)


        //Handling custom calendar events
        binding.calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {
                val df = SimpleDateFormat("dd-MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }

            override fun onMonthChanged(date: Date?) {
                val df = SimpleDateFormat("MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }
        })



        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}