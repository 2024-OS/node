package com.example.node_project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.node_project.databinding.CustomMarkerBinding
import com.google.android.gms.maps.model.Marker

// RecyclerView를 위한 어댑터 클래스
class PlanList2Adapter(
    private var markers: List<Marker>, // 현재 마커 목록
    private val onMarkerClick: (Marker) -> Unit // 마커 클릭 시 호출되는 함수
) : RecyclerView.Adapter<PlanList2Adapter.MarkerViewHolder>() {

    // ViewHolder 클래스 정의
    inner class MarkerViewHolder(private val binding: CustomMarkerBinding) : RecyclerView.ViewHolder(binding.root) {
        // 마커 데이터를 뷰에 바인딩
        fun bind(marker: Marker) {
            binding.markerTextView.text = marker.title // 마커 제목 설정
            binding.root.setOnClickListener {
                onMarkerClick(marker) // 마커 클릭 시 콜백 호출
            }
        }
    }

    // ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val binding = CustomMarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false) // XML 레이아웃 인플레이트
        return MarkerViewHolder(binding) // ViewHolder 반환
    }

    // ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markers[position]) // 각 마커를 ViewHolder에 바인딩
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = markers.size // 현재 마커 개수 반환

    // 마커 목록 업데이트 함수
    @SuppressLint("NotifyDataSetChanged")
    fun updateMarkers(newMarkers: List<Marker>) {
        markers = newMarkers // 새로운 마커 목록으로 업데이트
        notifyDataSetChanged() // RecyclerView에 데이터 변경 알림
    }
}
