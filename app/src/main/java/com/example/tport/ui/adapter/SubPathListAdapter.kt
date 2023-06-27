package com.example.tport.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tport.databinding.SubPathListItemBinding
import com.example.tport.network.dto.SubPath

class SubPathListAdapter(
): ListAdapter<SubPath, SubPathListAdapter.SubPathViewHolder>(DiffCallback) {
    class SubPathViewHolder(private val binding: SubPathListItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(subPath: SubPath){
            binding.apply {
                val travelTimeString = subPath.travelTime.toString() + "분"
                val subPathNameString = when (subPath.vehicle.type) {
                    "WALK" -> {
                        "도보"
                    }
                    "SUBWAY" -> {
                        "지하철"
                    }
                    "BUS" -> {
                        "버스" + subPath.vehicle.busNum
                    }
                    else -> {
                        "버스" + subPath.vehicle.busNum
                    }
                }
                method.text = subPathNameString
                travelTime.text = travelTimeString
                startPoint.text = subPath.getOnBusStop
                if(subPath.vehicle.type == "M_BUS"){
                    val busName = "버스" + subPath.vehicle.busNum.toString()
                    val busStopList = subPath.vehicle.busStop
                    var busArrivalTimeString = ""
                    for (busStop in busStopList!!) {
                        if (busStop.name == subPath.getOnBusStop){
                            busArrivalTimeString = busStop.busArrivalTime + "도착"
                        }

                    }
                    waitingBus.text = busName
                    busArrivalTime.text = busArrivalTimeString
                }
            }
        }
    }

    private var _viewBinding: SubPathListItemBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubPathListAdapter.SubPathViewHolder {
        _viewBinding = SubPathListItemBinding.inflate(LayoutInflater.from(parent.context))
        return SubPathListAdapter.SubPathViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SubPathListAdapter.SubPathViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SubPath>() {
            override fun areItemsTheSame(oldItem: SubPath, newItem: SubPath): Boolean {
                return oldItem.getOnBusStop == newItem.getOnBusStop
            }

            override fun areContentsTheSame(oldItem: SubPath, newItem: SubPath): Boolean {
                return oldItem == newItem
            }
        }
    }
}