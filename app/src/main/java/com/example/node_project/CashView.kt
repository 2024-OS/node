package com.example.node_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.app.Activity
import com.bumptech.glide.Glide
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

class CashView : Fragment() {
    private lateinit var etDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etContent: EditText
    private lateinit var ivImage: ImageView

    private val viewModel: CashViewModel by viewModels()

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cash_item, container, false)

        etDate = view.findViewById(R.id.etDate)
        etAmount = view.findViewById(R.id.etAmount)
        etContent = view.findViewById(R.id.etContent)
        ivImage = view.findViewById(R.id.ivImage)

        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnComplete = view.findViewById<Button>(R.id.btnComplete)

        val key = arguments?.getString("key")
        key?.let { viewModel.loadItem(it) }

        setupObservers()

        btnComplete.setOnClickListener {
            viewModel.saveItem(
                etDate.text.toString(),
                etAmount.text.toString(),
                etContent.text.toString(),
                imageUri
            )
            findNavController().popBackStack()
        }

        btnDelete.setOnClickListener {
            viewModel.deleteItem()
            findNavController().popBackStack()
        }

        ivImage.setOnClickListener {
            openGallery()
        }

        return view
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            ivImage.setImageURI(imageUri)
        }
    }

    private fun setupObservers() {
        viewModel.date.observe(viewLifecycleOwner, Observer {
            etDate.setText(it)
        })

        viewModel.amount.observe(viewLifecycleOwner, Observer {
            etAmount.setText(it)
        })

        viewModel.content.observe(viewLifecycleOwner, Observer {
            etContent.setText(it)
        })

        viewModel.imageUrl.observe(viewLifecycleOwner, Observer { url ->
            if (url.isNotEmpty()) {
                // Load image using Glide or similar library
                Glide.with(this).load(url).into(ivImage)
            } else {
                ivImage.setImageResource(R.drawable.picture)
            }
        })
    }
}
