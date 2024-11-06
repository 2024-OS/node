package com.example.node_project
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.node_project.R

class BudgetFragment: Fragment() {

    private lateinit var budgetTable: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        // Find the budget table and add button in the layout
        budgetTable = view.findViewById(R.id.budgetTable)
        val addButton: Button = view.findViewById(R.id.Addbutton)

        // Set onClickListener for the add button
        addButton.setOnClickListener {
            addNewBudgetRow()
        }

        return view
    }

    private fun addNewBudgetRow() {
        // Create a new TableRow
        val newRow = TableRow(context)
        newRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        // Create and configure the new CheckBox
        val checkBox = CheckBox(context)
        newRow.addView(checkBox)

        // Create and configure the item name EditText
        val itemName = EditText(context)
        itemName.hint = "물품"
        itemName.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemName)

        // Create and configure the quantity EditText
        val itemQuantity = EditText(context)
        itemQuantity.hint = "수량"
        itemQuantity.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemQuantity)

        // Create and configure the price EditText
        val itemPrice = EditText(context)
        itemPrice.hint = "가격"
        itemPrice.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemPrice)

        // Add the new row to the budget table
        budgetTable.addView(newRow)
    }
}
