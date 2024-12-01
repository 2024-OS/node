package com.example.node_project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.CustomMarkerBinding
import com.google.android.gms.maps.model.Marker

// RecyclerView를 위한 어댑터 클래스
class PlanMarkerListAdapter(
    private var markers: List<Marker>,
    private val onMarkerClick: (Marker) -> Unit
) : RecyclerView.Adapter<PlanMarkerListAdapter.MarkerViewHolder>() {

    // ViewHolder 클래스 정의
    inner class MarkerViewHolder(private val binding: CustomMarkerBinding) : RecyclerView.ViewHolder(binding.root) {
        // 마커 데이터를 뷰에 바인딩
        fun bind(marker: Marker) {
            binding.markerTextView.text = marker.title
            binding.root.setOnClickListener {
                onMarkerClick(marker)
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val binding = CustomMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarkerViewHolder(binding)
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markers[position])
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = markers.size

    // 마커 목록 업데이트 함수
    @SuppressLint("NotifyDataSetChanged")
    fun updateMarkers(newMarkers: List<Marker>) {
        markers = newMarkers
        notifyDataSetChanged()
    }
}
