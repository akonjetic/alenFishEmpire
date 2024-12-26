package com.example.alenfishempire.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.generateViewId
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.viewmodel.NewCalculationViewModel
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding

class NewCalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCalculationBinding
    private val viewModel: NewCalculationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCalculationBinding.inflate(layoutInflater)
        val view = binding.root

        viewModel.fetchAllFish(this)

        setContentView(view)

        setRowElementIds(binding.editableRow, 0)

        val initialRowQuantity = binding.tableLayout.findViewWithTag<EditText>("fishQuantity_0")
        initialRowQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                calculateTotalPrice(0)
                calculateGrandTotal()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val initialIsFree = binding.tableLayout.findViewWithTag<CheckBox>("fishIsFree_0")
        initialIsFree.setOnCheckedChangeListener { _, isChecked ->
            calculateTotalPrice(0)
            calculateGrandTotal()
        }
        calculateGrandTotal()

        setupSpinner(binding.fishName, 0)


        binding.addRowButton.setOnClickListener {
            // inicijalni red
            val originalRow = binding.editableRow

            // kopiranje inicijalnog
            val newRow = TableRow(this)
            for (i in 0 until originalRow.childCount) {
                val view = originalRow.getChildAt(i)
                val newView = when (view) {
                    is Spinner -> Spinner(this).apply {
                        adapter = view.adapter
                        setupSpinner(this, binding.tableLayout.childCount - 1)
                    }

                    is EditText -> EditText(this).apply {
                        layoutParams = view.layoutParams
                        inputType = view.inputType
                        isFocusable = view.isFocusable
                        isEnabled = view.isEnabled
                        addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                calculateTotalPrice(binding.tableLayout.indexOfChild(newRow))
                                calculateGrandTotal()
                            }

                            override fun afterTextChanged(s: Editable?) {}
                        })
                    }

                    is CheckBox -> CheckBox(this).apply {
                        isChecked = false
                        layoutParams = view.layoutParams
                        setOnCheckedChangeListener { _, isChecked ->
                            calculateTotalPrice(
                                binding.tableLayout.indexOfChild(
                                    newRow
                                )
                            )
                            calculateGrandTotal()
                        }
                    }

                    is ImageView -> ImageView(this).apply {
                        layoutParams = view.layoutParams
                        setImageResource(R.drawable.ic_delete_row)
                        setOnClickListener {
                            binding.tableLayout.removeView(newRow)
                            calculateGrandTotal()
                        }
                        setPadding(
                            view.paddingLeft,
                            view.paddingTop,
                            view.paddingRight,
                            view.paddingBottom
                        )
                    }

                    else -> TextView(this).apply {
                        text = "0"
                        layoutParams = view.layoutParams
                        gravity = view.foregroundGravity
                    }
                }
                newRow.addView(newView)
            }

            setRowElementIds(newRow, binding.tableLayout.childCount - 1)

            // dodavanje novog iznad gumba
            val index = binding.tableLayout.childCount - 1
            binding.tableLayout.addView(newRow, index)
        }


    }

    fun setupSpinner(spinner: Spinner, rowIndex: Int) {
        viewModel.listOfAllFish.observe(this) { fishList ->
            Log.d("NewCalculationActivity", "Fish list size: ${fishList.size}")
            fishList.forEach { Log.d("NewCalculationActivity", "Fish name: ${it.name}") }

            val fishNames = fishList.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fishNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedFish = fishList[position]
                    val priceTextView =
                        binding.tableLayout.findViewWithTag<TextView>("price_1_$rowIndex")
                    priceTextView?.text = selectedFish.price.toString()
                    calculateTotalPrice(rowIndex)
                    calculateGrandTotal()
                }

                override fun onNothingSelected(parent: AdapterView<*>) { //    Do nothing } }
                }
            }
        }
    }

    private fun setRowElementIds(row: TableRow, rowIndex: Int) {
        var textViewCounter = 0
        for (i in 0 until row.childCount) {
            val view = row.getChildAt(i)
            view.id = generateViewId()
            val type = when (view) {
                is Spinner -> "fishName_"
                is EditText -> "fishQuantity_"
                is CheckBox -> "fishIsFree_"
                is ImageView -> "imageview_"
                is TextView -> {
                    textViewCounter++
                    "price_${textViewCounter}_"
                }

                else -> "unknown"
            }
            view.tag = "$type$rowIndex"
            Log.d("NewCalculationActivity", view.tag.toString())
        }
    }

    private fun calculateTotalPrice(rowIndex: Int) {
        val quantityEditText =
            binding.tableLayout.findViewWithTag<EditText>("fishQuantity_$rowIndex")
        val priceTextView = binding.tableLayout.findViewWithTag<TextView>("price_1_$rowIndex")
        val totalTextView = binding.tableLayout.findViewWithTag<TextView>("price_2_$rowIndex")
        val checkBox = binding.tableLayout.findViewWithTag<CheckBox>("fishIsFree_$rowIndex")
        if (quantityEditText != null && priceTextView != null && totalTextView != null && checkBox != null) {
            if (checkBox.isChecked) {
                totalTextView.text = "0.0"
            } else {
                val quantityText = quantityEditText.text.toString()
                val priceText = priceTextView.text.toString()
                val quantity = if (quantityText.isNotEmpty()) quantityText.toInt() else 0
                val price = if (priceText.isNotEmpty()) priceText.toDouble() else 0.0
                val total = quantity * price
                totalTextView.text = total.toString()
            }
        }
    }


    private fun calculateGrandTotal() {
        var grandTotal = 0.0
        var grandTotalQuantity = 0
        for (i in 0 until binding.tableLayout.childCount) {
            val totalTextView = binding.tableLayout.findViewWithTag<TextView>("price_2_$i")
            val totalQuantiyView = binding.tableLayout.findViewWithTag<EditText>("fishQuantity_$i")

            val totalText = totalTextView?.text?.toString()
            val totalQuantity = totalQuantiyView?.text?.toString()

            grandTotal += if (!totalText.isNullOrEmpty()) totalText.toDoubleOrNull() ?: 0.0 else 0.0
            grandTotalQuantity += if (!totalQuantity.isNullOrEmpty()) totalQuantity.toIntOrNull() ?: 0 else 0
        }
        binding.totalAmount.text = grandTotal.toString()
        binding.totalQuantity.text = grandTotalQuantity.toString()
    }

}
/*  TODO - error na brisanju reda, ne racuna dobro poslije */