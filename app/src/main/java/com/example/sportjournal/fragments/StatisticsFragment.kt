package com.example.sportjournal.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportjournal.R
import com.example.sportjournal.StatisticsAdapter
import com.example.sportjournal.StatisticsViewModel
import com.example.sportjournal.databinding.FragmentStatisticsBinding
import com.example.sportjournal.utilits.*
import com.google.firebase.database.DatabaseReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by activityViewModels()
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var workoutsReference: DatabaseReference
    private lateinit var statisticsAdapter: StatisticsAdapter
    private var dateFrom = "01.01.1900"
    private var dateTo = "31.12.2100"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStatisticsBinding.bind(requireView())
        workoutsReference =
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(NODE_WORKOUTS)

        getTotalWeight()
        getStatistics()

        binding.rb1.setOnClickListener {
            binding.dateLayout.visibility = View.GONE
            dateFrom = "01.01.1900"
            dateTo = "31.12.2100"
            getTotalWeight()
            getStatistics()
        }
        binding.rb2.setOnClickListener {
            binding.dateLayout.visibility = View.VISIBLE
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.dateBtn1.setOnClickListener {
            val dpd = DatePickerDialog(this.requireContext(), { _, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val mmmMonth = if (mmMonth >= 10) mmMonth.toString() else "0$mmMonth"
                val mmDay = if (mDay >= 10) mDay.toString() else "0$mDay"
                binding.dateTV1.setText("$mmDay.$mmmMonth.$mYear")
                dateFrom = "$mmDay.$mmmMonth.$mYear"
            }, year, month, day)
            dpd.show()
        }

        binding.dateBtn2.setOnClickListener {
            val dpd = DatePickerDialog(this.requireContext(), { _, mYear, mMonth, mDay ->
                val mmMonth = mMonth + 1
                val mmmMonth = if (mmMonth >= 10) mmMonth.toString() else "0$mmMonth"
                val mmDay = if (mDay >= 10) mDay.toString() else "0$mDay"
                binding.dateTV2.setText("$mmDay.$mmmMonth.$mYear")
                dateTo = "$mmDay.$mmmMonth.$mYear"
            }, year, month, day)
            dpd.show()
        }

        binding.refreshButton.setOnClickListener {
            getTotalWeight()
            getStatistics()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStatistics() {
        viewModel.statistics.clear()
        workoutsReference.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            val weight = Array(10) { 0f }

            ds1.children.forEach { ds2 ->
                if (isBetween(ds2.child(WORKOUT_DATE).value.toString())) {
                    ds2.child(WEIGHT).children.forEach { ds3 ->
                        if (ds3.key != "totalWeight") {
                            when (ds3.key) {
                                "group1Weight" -> {
                                    weight[1] += ds3.value.toString().toFloat()
                                }
                                "group2Weight" -> {
                                    weight[2] += ds3.value.toString().toFloat()
                                }
                                "group3Weight" -> {
                                    weight[3] += ds3.value.toString().toFloat()
                                }
                                "group4Weight" -> {
                                    weight[4] += ds3.value.toString().toFloat()
                                }
                                "group5Weight" -> {
                                    weight[5] += ds3.value.toString().toFloat()
                                }
                                "group6Weight" -> {
                                    weight[6] += ds3.value.toString().toFloat()
                                }
                                "group7Weight" -> {
                                    weight[7] += ds3.value.toString().toFloat()
                                }
                                "group8Weight" -> {
                                    weight[8] += ds3.value.toString().toFloat()
                                }
                                "group9Weight" -> {
                                    weight[9] += ds3.value.toString().toFloat()
                                }
                                else -> weight[0] += ds3.value.toString().toFloat()
                            }
                        }
                    }
                }

            }
            val exType = resources.getStringArray(R.array.exercise_types_array)
            for (i in 0..9) viewModel.statistics.add(Pair(exType[i], weight[i]))
            statisticsAdapter.notifyDataSetChanged()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalWeight() {
        var totalWeight = 0f
        var notNull = false
        binding.totalWeight.text =
            resources.getString(R.string.totalWeight, 0)
        workoutsReference.addListenerForSingleValueEvent(AppValueEventListener { ds1 ->
            ds1.children.forEach { ds2 ->
                if (isBetween(ds2.child(WORKOUT_DATE).value.toString())){
                    totalWeight += ds2.child(WEIGHT).child("totalWeight").value.toString().toFloat()
                    notNull = true
                }
            }
            if (!notNull)  totalWeight = 1f
            statisticsAdapter = StatisticsAdapter(
                viewModel.statistics,
                totalWeight,
                requireContext()
            )
            val statisticsRV = binding.statisticsRV
            statisticsRV.layoutManager = LinearLayoutManager(context)
            statisticsRV.adapter = statisticsAdapter

            if (notNull) binding.totalWeight.text =
                resources.getString(R.string.totalWeight, totalWeight.roundToInt())
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBetween(dateStr: String): Boolean {
        return if (dateStr == "Дата не указана" || dateStr == "Date not given"){
            false
        } else{
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val date = LocalDate.parse(dateStr, dateFormatter)
            val dateFromFormatted = LocalDate.parse(dateFrom, dateFormatter)
            val dateToFormatted = LocalDate.parse(dateTo, dateFormatter)
            (date in dateFromFormatted..dateToFormatted)
        }

    }

}