package com.example.alenfishempire.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.viewmodel.OrderHistoryViewModel
import com.example.alenfishempire.adapter.OrderHistoryAdapter
import com.example.alenfishempire.databinding.ActivityOrderHistoryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private val viewModel: OrderHistoryViewModel by viewModels()
    private val orderHistoryAdapter by lazy { OrderHistoryAdapter(this, arrayListOf()) }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        binding.ordersRecyclerView.adapter = orderHistoryAdapter
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        val view = binding.root

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.sort_options,
            R.layout.spinner_dropdown_item
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.sortSpinner.adapter = adapter

        applyFilters()
        setContentView(view)

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setDefaultDateRange()

        binding.startDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.startDate.text = selectedDate
                fetchFilteredAndSortedOrders()
            }
        }

        binding.endDate.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.endDate.text = selectedDate
                fetchFilteredAndSortedOrders()
            }
        }
        viewModel.getAllOrders(this)

        binding.sortSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                fetchFilteredAndSortedOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        fetchFilteredAndSortedOrders()
        viewModel.getAllOrders(this)
    }

    private fun setDefaultDateRange() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Prvi dan mjeseca
        val startDate = dateFormat.format(calendar.time)

        calendar.set(
            Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        ) // Zadnji dan mjeseca
        val endDate = dateFormat.format(calendar.time)

        binding.startDate.text = startDate
        binding.endDate.text = endDate
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = dateFormat.format(Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun fetchFilteredAndSortedOrders() {
        val startDate = binding.startDate.text.toString()
        val endDate = binding.endDate.text.toString()
        val sortBy = getSortQueryKey(binding.sortSpinner.selectedItem.toString())

        viewModel.filterOrdersByDateRange(this, startDate, endDate, sortBy) { data ->
            orderHistoryAdapter.updateData(data)
        }
    }

    private fun applyFilters() {
        fetchFilteredAndSortedOrders()
    }

    fun getSortQueryKey(selectedSortOption: String): String {
        return when (selectedSortOption) {
            "Date (descending)" -> "date_desc"
            "Date (ascending)" -> "date_asc"
            "Quantity (descending)" -> "quantity"
            "Total Price (descending)" -> "price"
            else -> "date_desc"
        }
    }
}
