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
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        budgetTable = view.findViewById(R.id.budgetTable)
        val addButton: Button = view.findViewById(R.id.Addbutton)

        addButton.setOnClickListener {
            addNewBudgetRow()
        }

        return view
    }

    private fun addNewBudgetRow() {
        val newRow = TableRow(context)
        newRow.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        val checkBox = CheckBox(context)
        newRow.addView(checkBox)

        val itemName = EditText(context)
        itemName.hint = "물품"
        itemName.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemName)

        val itemQuantity = EditText(context)
        itemQuantity.hint = "수량"
        itemQuantity.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemQuantity)

        val itemPrice = EditText(context)
        itemPrice.hint = "가격"
        itemPrice.layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
        newRow.addView(itemPrice)

        budgetTable.addView(newRow)
    }
}
