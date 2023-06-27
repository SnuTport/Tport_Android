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

/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.retrievePath(navigationArgs.path).observe(this.viewLifecycleOwner){
            bind(it)
            path = it
            var methodList = viewModel.getMethodList(it)
            val adapter = MethodListAdapter(
                onItemClicked = {

                },
                onButtonClicked = {
                    updateReservedData(path)
                    methodList = viewModel.getMethodList(path)
                }
            )
            binding.recyclerview.adapter = adapter
            binding.recyclerview.layoutManager = LinearLayoutManager(this.context)
            adapter.submitList(methodList)
        }
        binding.upButton.setOnClickListener{
            findNavController().navigateUp()
        }

    }

    fun bind(path: Path0){
        val hourArrival = path.tportArrivalTime/60
        val minArrival = path.tportArrivalTime%60
        val timeArrival = hourArrival.toString() + "시 " + minArrival.toString() + "분"
        val listArrivalTime: List<String> = listOf("ㅣ", timeArrival, "도착", "ㅣ")
        val hourTravel = path.tportTravelTime/60
        val minTravel = path.tportTravelTime%60
        val timeTravel = hourTravel.toString() + "시간 " + minTravel.toString() + "분"
        binding.apply {
            totalTravelTime.text = timeTravel
            finalArrivalTime.text = listArrivalTime.joinToString(" ")
            fare.text = path.fare
            travelSequence.text = concatenateSequence(
                path.method1, path.travelTime1, path.method2, path.travelTime2, path.method3,
                path.travelTime3, path.method4, path.travelTime4, path.method5, path.travelTime5,
                path.method6, path.travelTime6
            )
            arrivalTime.text = timeArrival
            destination.text = path.destination
        }
    }

    private fun concatenateSequence(
        s1: String, s2: String, s3: String, s4: String, s5: String, s6: String,
        s7: String, s8: String, s9: String, s10: String, s11: String, s12: String,
    ): String {
        val input = listOf(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12)
        val output: MutableList<String> = mutableListOf()
        for (i in input.indices) {
            if (input[i] != "None" && input[i] != "NoneNone" && input[i] != "" ) {

                if (i%2 == 1) {
                    output.add(input[i-1]+" "+input[i]+"분")
                }
            }
        }
        return output.joinToString("  →  ")
    }

    private fun updateReservedData(path: Path0) {
        viewModel.updateReservedNum(path.id)
    }
*/
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
            if (busStop.name == path.getOnBusStop){
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
        } else if ( busEmptyNum < busDemand && busDemand < 2*busEmptyNum ){
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