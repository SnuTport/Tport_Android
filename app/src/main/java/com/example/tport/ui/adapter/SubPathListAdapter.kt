package com.example.tport.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tport.databinding.MethodListItemBinding
import com.example.tport.network.dto.SubPath

class SubPathListAdapter(
    private val onItemClicked:(SubPath) -> Unit, private val onButtonClicked: (SubPath) -> Unit,
): ListAdapter<SubPath, SubPathListAdapter.SubPathViewHolder>(DiffCallback) {
    class SubPathViewHolder(private val binding: MethodListItemBinding):
        RecyclerView.ViewHolder(binding.root){
        val button = binding.reserveButton

        fun bind(subPath: SubPath){

        }
    }

    private var _viewBinding: MethodListItemBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubPathListAdapter.SubPathViewHolder {
        _viewBinding = MethodListItemBinding.inflate(LayoutInflater.from(parent.context))
        return SubPathListAdapter.SubPathViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: SubPathListAdapter.SubPathViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        holder.button.setOnClickListener {
            onButtonClicked(current)
        }
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