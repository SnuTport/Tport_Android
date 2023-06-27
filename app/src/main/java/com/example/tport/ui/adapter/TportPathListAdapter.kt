package com.example.tport.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tport.network.dto.previous.Path0
import com.example.tport.databinding.PathListItemBinding
import com.example.tport.network.dto.BusStopInDetail
import com.example.tport.network.dto.Path
import com.example.tport.network.dto.SubPath

class TportPathListAdapter(
    private val onItemClicked:(Path) -> Unit
): ListAdapter<Path, TportPathListAdapter.PathViewHolder>(DiffCallback) {

    class PathViewHolder(private val binding: PathListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(path: Path){
            val busStopList: List<BusStopInDetail> = path.metroBusDetail.busStop
            val subPathList: List<SubPath> = path.subPaths
            val travelSequenceList = mutableListOf<String>()
            for (subPath in subPathList) {
                travelSequenceList.add(subPath.vehicle.type + " " + subPath.travelTime.toString() + "분")
                Log.d("TravelSequence", "$travelSequenceList")
            }
            var busArrivalTime = ""
            var busEmptyNum = 45
            var busDemand = 0
            var busReservedNum = 0
            var busUnreservedNum = 0

            for (busStop in busStopList) {
                if (busStop.name == path.getOnBusStop){
                    busArrivalTime = busStop.busArrivalTime
                    busEmptyNum = busStop.forecastingBusStopData.emptyNum
                    busDemand = busStop.forecastingBusStopData.demand
                    busReservedNum = busStop.forecastingBusStopData.reservedNum
                    busUnreservedNum = busStop.forecastingBusStopData.unreservedNum
                }

            }
            val listArrivalTime: List<String> = listOf("ㅣ", busArrivalTime, "도착", "ㅣ")
            val hourTravel = path.travelTime/60
            val minTravel = path.travelTime%60
            val timeTravel = hourTravel.toString() + "시간 " + minTravel.toString() + "분"

            binding.apply {
                val waitingConditionString = if (busDemand <= busEmptyNum / 2) {
                    "여유"
                } else if (busEmptyNum / 2 < busDemand && busDemand <= busEmptyNum) {
                    "혼잡"
                } else {
                    "포화"
                }
                waitingCondition.text = waitingConditionString
                when (waitingCondition.text) {
                    "여유" -> {
                        waitingCondition.setTextColor(Color.parseColor("#00FF00")) // 연두색
                    }
                    "혼잡" -> {
                        waitingCondition.setTextColor(Color.parseColor("#FF7F00")) // 주황색
                    }
                    else -> {
                        waitingCondition.setTextColor(Color.parseColor("#FF0000")) // 빨간색
                    }
                }

                totalTravelTime.text = timeTravel
                finalArrivalTime.text = listArrivalTime.joinToString(" ")
                val fareString = path.fare.toString() + "원"
                fare.text = fareString
                travelSequence.text = travelSequenceList.joinToString(" → ")
            }
        }
    }



    private var _viewBinding: PathListItemBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PathViewHolder {
        _viewBinding = PathListItemBinding.inflate(LayoutInflater.from(parent.context))
        return PathViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Path>() {
            override fun areItemsTheSame(oldItem: Path, newItem: Path): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Path, newItem: Path): Boolean {
                return oldItem == newItem
            }
        }
    }

}