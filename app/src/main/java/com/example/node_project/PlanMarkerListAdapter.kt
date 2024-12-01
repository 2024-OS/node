package com.example.node_project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.CustomMarkerBinding
import com.google.android.gms.maps.model.Marker

class PlanMarkerListAdapter(
    private var markers: List<Marker>,
    private val onMarkerClick: (Marker) -> Unit
) : RecyclerView.Adapter<PlanMarkerListAdapter.MarkerViewHolder>() {

    inner class MarkerViewHolder(private val binding: CustomMarkerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(marker: Marker) {
            binding.markerTextView.text = marker.title
            binding.root.setOnClickListener {
                onMarkerClick(marker)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val binding = CustomMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarkerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markers[position])
    }

    override fun getItemCount(): Int = markers.size

    fun updateMarkers(newMarkers: List<Marker>) {
        markers = newMarkers
        notifyDataSetChanged()
    }
}