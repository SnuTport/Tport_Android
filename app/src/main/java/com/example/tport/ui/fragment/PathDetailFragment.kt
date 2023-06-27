package com.example.tport.ui.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tport.MapFragmentActivity
import com.example.tport.MyApplication
import com.example.tport.databinding.FragmentPathDetailBinding
import com.example.tport.network.dto.BusStopInDetail
import com.example.tport.network.dto.Path
import com.example.tport.network.dto.SubPath
import com.example.tport.network.dto.previous.Path0
import com.example.tport.ui.PathFindingViewModel
import com.example.tport.ui.PathFindingViewModelFactory
import com.example.tport.ui.adapter.MethodListAdapter
import com.example.tport.ui.adapter.SubPathListAdapter
import com.example.tport.viewmodel.PathViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PathDetailFragment : Fragment() {

    lateinit var mainActivity: MapFragmentActivity
    lateinit var path: Path0
    private var _binding: FragmentPathDetailBinding? = null
    private val binding get() = _binding!!
    private val navigationArgs: PathDetailFragmentArgs by navArgs()
    private val viewModel: PathViewModel by viewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MapFragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedPath: Path = navigationArgs.path!!
        val selectedTime: String = navigationArgs.time

        val adapter = SubPathListAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this.context)

        binding.reserveButton.setOnClickListener{
            lifecycleScope.launch {
                viewModel.reservePath(selectedPath, selectedTime)
                viewModel.getPath(selectedPath, selectedTime)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getPath(selectedPath, selectedTime)
            viewModel.path.collect {
                bind(it!!)
                adapter.submitList(it.subPaths)
            }
        }

        binding.upButton.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun bind(path: Path){
        val busStopList: List<BusStopInDetail> = path.metroBusDetail.busStop
        val subPathList: List<SubPath> = path.subPaths
        val travelSequenceList = mutableListOf<String>()
        for (subPath in subPathList) {
            travelSequenceList.add(subPath.vehicle.type + " " + subPath.travelTime.toString() + "분")
            Log.d("TravelSequence", "$travelSequenceList")
        }
        val destinationString = subPathList[subPathList.size-1].getOffBusStop
        var busArrivalTimeString = ""
        var finalArrivalTimeString = ""
        var busEmptyNum = 45
        var busDemand = 0
        var busReservedNum = 0

        for (busStop in busStopList) {
            if (busStop.name == path.metroSubPath.getOnBusStop){
                busArrivalTimeString = busStop.busArrivalTime
                busEmptyNum = busStop.forecastingBusStopData.emptyNum
                busDemand = busStop.forecastingBusStopData.demand
                busReservedNum = busStop.forecastingBusStopData.reservedNum
            } else if( busStop.name == path.getOffBusStop){
                finalArrivalTimeString = busStop.busArrivalTime
            }

        }
        val listArrivalTime: List<String> = listOf("ㅣ", busArrivalTimeString, "도착", "ㅣ")
        val hourTravel = path.travelTime/60
        val minTravel = path.travelTime%60
        val timeTravel = hourTravel.toString() + "시간 " + minTravel.toString() + "분"
        val fareString = path.fare.toString() + "원"
        val reservedNumString = "예약인원 " + busReservedNum.toString() + "명"
        val emptyNumAndString = "빈자리수 " + busEmptyNum.toString() + "석" + " / " + "대기인원 "
        val waitingTimeString = if ( busDemand <= busEmptyNum ) {
            "대기시간 15분"
        } else if ( busDemand < 2*busEmptyNum ){
            "대기시간 30분"
        } else {
            "대기시간 45분"
        }

        binding.apply {
            totalTravelTime.text = timeTravel
            finalArrivalTime.text = listArrivalTime.joinToString(" ")
            fare.text = fareString
            travelSequence.text = travelSequenceList.joinToString(" → ")
            reservedNum.text = reservedNumString
            metroBusNum.text = path.metroBusDetail.busNum
            emptyNumAnd.text = emptyNumAndString
            waitingTime.text = waitingTimeString
            destination.text = destinationString
            val waitingConditionText = if (busDemand <= busEmptyNum / 2) {
                "여유"
            } else if (busEmptyNum / 2 < busDemand && busDemand <= busEmptyNum) {
                "혼잡"
            } else {
                "포화"
            }
            waitingCondition.text = waitingConditionText
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
        }
    }
}