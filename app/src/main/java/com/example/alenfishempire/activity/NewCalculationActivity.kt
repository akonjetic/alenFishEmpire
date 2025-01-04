package com.example.alenfishempire.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alenfishempire.R
import com.example.alenfishempire.activity.viewmodel.NewCalculationViewModel
import com.example.alenfishempire.database.entities.Fish
import com.example.alenfishempire.database.entities.FishOrder
import com.example.alenfishempire.database.entities.Order
import com.example.alenfishempire.databinding.ActivityNewCalculationBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

const val EXTRA_IS_READ_ONLY = "READ_ONLY"
const val EXTRA_ORDER = "ORDER"

class NewCalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewCalculationBinding
    private val viewModel: NewCalculationViewModel by viewModels()
    private val fishNameToObjectMap = mutableMapOf<String, Fish>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewCalculationBinding.inflate(layoutInflater)
        val view = binding.root

        viewModel.fetchAllFish(this)

        setContentView(view)

        val isReadOnly = intent.getBooleanExtra(EXTRA_IS_READ_ONLY, false)
        val order = intent.getSerializableExtra(EXTRA_ORDER) as? Order

        if (isReadOnly) {
            binding.addRowButton.visibility = View.GONE
            binding.sumUpOrder.visibility = View.GONE
            binding.editableRow.visibility = View.GONE
        } else {
            //setup inicijalnog retka
            setupInitialRow()

            //setup gumbica
            binding.sumUpOrder.setOnClickListener {
                saveOrder()
            }
            binding.addRowButton.setOnClickListener {
                addNewRow()
            }
        }

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupInitialRow() {
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
        initialIsFree.setOnCheckedChangeListener { _, _ ->
            calculateTotalPrice(0)
            calculateGrandTotal()
        }
        calculateGrandTotal()
        setupSpinner(binding.fishName, 0)
    }

    private fun addNewRow() {
        val originalRow = binding.editableRow

        val newRow = TableRow(this)
        for (i in 0 until originalRow.childCount) {
            val newView = when (val view = originalRow.getChildAt(i)) {
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
                    setOnCheckedChangeListener { _, _ ->
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
                        reindexRows()
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

        val index = binding.tableLayout.childCount - 1
        binding.tableLayout.addView(newRow, index)
    }

    private fun setupReadOnlyTable (order: Order){


    }

    private fun setupSpinner(spinner: Spinner, rowIndex: Int) {
        viewModel.listOfAllFish.observe(this) { fishList ->
            Log.d("NewCalculationActivity", "Fish list size: ${fishList.size}")
            fishList.forEach { Log.d("NewCalculationActivity", "Fish name: ${it.name}") }
            fishNameToObjectMap.clear()

            val fishNames = fishList.map { fish ->
                fishNameToObjectMap[fish.name] = fish
                fish.name
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fishNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private fun calculateGrandTotal() {
        var grandTotal = 0.0
        var grandTotalQuantity = 0
        for (i in 0 until binding.tableLayout.childCount) {
            val totalTextView = binding.tableLayout.findViewWithTag<TextView>("price_2_$i")
            val totalQuantiyView = binding.tableLayout.findViewWithTag<EditText>("fishQuantity_$i")

            val totalText = totalTextView?.text?.toString()
            val totalQuantity = totalQuantiyView?.text?.toString()

            grandTotal += if (!totalText.isNullOrEmpty()) totalText.toDoubleOrNull() ?: 0.0 else 0.0
            grandTotalQuantity += if (!totalQuantity.isNullOrEmpty()) totalQuantity.toIntOrNull()
                ?: 0 else 0
        }
        binding.totalAmount.text = grandTotal.toString()
        binding.totalQuantity.text = grandTotalQuantity.toString()
    }

    private fun reindexRows() {
        for (i in 0 until binding.tableLayout.childCount) {
            val row = binding.tableLayout.getChildAt(i) as TableRow
            setRowElementIds(row, i)
        }
        calculateGrandTotal()
    }

    private fun saveOrder() {
        val fishOrderList = mutableListOf<FishOrder>()
        val currentDate = Date()

        for (i in 0 until binding.tableLayout.childCount - 1) {
            val quantityEditText = binding.tableLayout.findViewWithTag<EditText>("fishQuantity_$i")
            val spinner = binding.tableLayout.findViewWithTag<Spinner>("fishName_$i")
            val isFreeCheckBox = binding.tableLayout.findViewWithTag<CheckBox>("fishIsFree_$i")

            if (quantityEditText != null && spinner != null && isFreeCheckBox != null) {
                val selectedFishName = spinner.selectedItem as? String
                val selectedFish = fishNameToObjectMap[selectedFishName]
                val quantityText = quantityEditText.text.toString()
                val quantity = if (quantityText.isNotEmpty()) quantityText.toInt() else 0
                val isFree = isFreeCheckBox.isChecked

                if (selectedFish != null && quantity > 0) {
                    val fishOrder = FishOrder(
                        id = 0,  // Auto-generated
                        fishId = selectedFish.id,
                        quantity = quantity,
                        isFree = isFree
                    )
                    viewModel.saveNewFishOrder(this, fishOrder) { fishOrderId ->
                        val newFishOrder = fishOrder.copy(id = fishOrderId)
                        fishOrderList.add(newFishOrder)

                        // Nakon što su svi FishOrder objekti dodani, nastavi s dodavanjem Ordera
                        if (fishOrderList.size == binding.tableLayout.childCount - 2) {
                            val fishOrderIds = fishOrderList.map { it.id }
                            val order = Order(
                                id = 0,  // Auto-generated
                                date = currentDate,
                                fishOrderId = fishOrderIds,
                                discount = null
                            )
                            viewModel.saveNewOrder(this, order) { orderId ->
                                order.id = orderId
                            }
                            showExportDialog(order)
                        } else {
                            Log.d(
                                "NewCalculationActivity",
                                "Nisam jos za spremanje, sad sam na: " + fishOrderList.size + "a trebam biti na: " + (binding.tableLayout.childCount - 1)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showExportDialog(order: Order) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Narudžba spremljena!")
        builder.setMessage("Želite li exportati narudžbu?")

        builder.setNegativeButton("Spremi u PDF") { _, _ ->
            exportToPDF(order)
        }

        builder.setPositiveButton("Ne, hvala") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        builder.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun exportToPDF(order: Order) {
        val fileName = "Order_${order.id}.pdf"
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, fileName)

        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy, HH:mm:ss") // Koristi "HH" za 24-satni format

        val formattedDate = dateFormat.format(order.date)

        document.add(Paragraph("Detalji narudzbe").setBold())
        document.add(Paragraph("ID narudzbe: ${order.id}"))
        document.add(Paragraph("Datum: $formattedDate"))

        document.add(Paragraph())
        document.add(Paragraph())

        val table = Table(floatArrayOf(100f, 100f, 100f, 100f, 100f))
        table.addHeaderCell("Vrsta").setBold()
        table.addHeaderCell("Kolicina").setBold()
        table.addHeaderCell("Cijena/Komad (€)").setBold()
        table.addHeaderCell("Gratis").setBold()
        table.addHeaderCell("Ukupno (€)").setBold()

        viewModel.fetchFishOrderDetails(this, order.fishOrderId) { detailsList ->
            detailsList.forEach { details ->
                val fishOrder = details["fishOrder"] as FishOrder
                val fish = details["fish"] as Fish

                table.addCell(fish.name).setTextAlignment(TextAlignment.CENTER)
                table.addCell(fishOrder.quantity.toString()).setTextAlignment(TextAlignment.CENTER)
                table.addCell(fish.price.toString()).setTextAlignment(TextAlignment.CENTER)
                table.addCell(if (fishOrder.isFree) "Da" else "Ne")
                    .setTextAlignment(TextAlignment.CENTER)
                table.addCell(if (fishOrder.isFree) "/" else (fish.price * fishOrder.quantity.toFloat()).toString())
                    .setTextAlignment(TextAlignment.CENTER)
            }

            val totalQuantity = order.fishOrderId.sumOf { fishOrderId ->
                val details =
                    detailsList.find { it["fishOrder"]?.let { fishOrder -> (fishOrder as FishOrder).id == fishOrderId } == true }
                val fishOrder = details?.get("fishOrder") as? FishOrder
                fishOrder?.quantity ?: 0
            }

            val totalPrice = order.fishOrderId.sumOf { fishOrderId ->
                val details =
                    detailsList.find { it["fishOrder"]?.let { fishOrder -> (fishOrder as FishOrder).id == fishOrderId } == true }
                val fishOrder = details?.get("fishOrder") as? FishOrder
                val fish = details?.get("fish") as? Fish
                if(fishOrder?.isFree == true){0.0} else{(fish?.price?.times(fishOrder?.quantity?.toFloat() ?: 0f) ?: 0.0).toDouble()}
            }

            document.add(table)
            document.add(Paragraph())
            document.add(Paragraph())
            document.add(Paragraph("Ukupna kolicina: $totalQuantity").setBold())
            document.add(Paragraph("Ukupna cijena (€): $totalPrice").setBold())
            document.close()

            MediaScannerConnection.scanFile(
                this,
                arrayOf(file.absolutePath),
                null,
                null
            )

            Toast.makeText(this, "Exported to $fileName", Toast.LENGTH_SHORT).show()
        }
    }
}
