package com.example.tport.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tport.MapFragmentActivity
import com.example.tport.databinding.FragmentPathFindingBinding
import com.example.tport.network.dto.Path
import com.example.tport.ui.adapter.NaverPathListAdapter
import com.example.tport.ui.adapter.TportPathListAdapter
import com.example.tport.viewmodel.PathViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.LocalTime


class PathFindingFragment : Fragment() {

    lateinit var mainActivity: MapFragmentActivity
    private var _binding: FragmentPathFindingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PathViewModel by viewModel()
//    private val viewModel: PathFindingViewModel by activityViewModels {
//        PathFindingViewModelFactory(
//            (activity?.application as MyApplication).database.pathDao(),
//        )
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MapFragmentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPathFindingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // date, time 정의
        var date = LocalDate.now()
        var time = LocalTime.now()
        var timeResult = LocalDateTime.of(date, time)
        val tportAdapter = TportPathListAdapter(
            onItemClicked = {
                Log.d("PathFindingFragment", "Path is clicked")
                navigateToDetail(it, timeResult.format(DateTimeFormatter.ISO_DATE_TIME))
            }
        )

        // 초기 adapter는 naverAdapter로 설정
        binding.recyclerview.adapter = tportAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this.context)

        binding.editTextOrgin.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                Log.d("EditText", "Origin OnFocus")
            }else{
                lifecycleScope.launch {
                    viewModel.searchPath(
                        binding.editTextOrgin.text.toString(),
                        binding.editTextDestination.text.toString(),
                        timeResult.format(DateTimeFormatter.ISO_DATE_TIME)
                    )
                }
            }
        }

        binding.editTextDestination.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                Log.d("EditText", "Destination OnFocus")
            }else{
                lifecycleScope.launch {
                    viewModel.searchPath(
                        binding.editTextOrgin.text.toString(),
                        binding.editTextDestination.text.toString(),
                        timeResult.format(DateTimeFormatter.ISO_DATE_TIME)
                    )
                }
            }
        }

        binding.dateButton.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                date = LocalDate.of(year, month+1, dayOfMonth)
                timeResult = LocalDateTime.of(date, time)
                Log.d("Time", "it is $timeResult")
            }
            val datePickerDialog = DatePickerDialog(
                mainActivity,
                dateSetListener, year, month, day
            )
            datePickerDialog.show()
        }

        binding.timeButton.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                time = LocalTime.of(hourOfDay, minute, time.second)
                timeResult = LocalDateTime.of(date, time)
                Log.d("Time", "it is $timeResult")
            }
            val timePickerDialog = TimePickerDialog(
                mainActivity,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        // show searched Path List
        lifecycleScope.launch {
            viewModel.pathList.collect {
                tportAdapter.submitList(it)
            }
        }

/*
        binding.timeButton.setOnClickListener{
            // recyclerView에 naverAdapter 연결
            binding.recyclerview.adapter = naverAdapter
            viewModel.getSearchedPathList(binding.editTextOrgin.text.toString(), binding.editTextDestination.text.toString(), "6시 45분")
            viewModel.getSearchedPathList("경기도 화성시 청계동 KCC스위첸아파트", "서울역1호선", "6시 45분")
            viewModel.searchedPathList.observe(this.viewLifecycleOwner) {
                viewModel.searchedPathList.let { naverAdapter.submitList( it.value) }
            }
        }
        binding.dateButton.setOnClickListener{
            // recyclerView에 tportAdapter 연결
            binding.recyclerview.adapter = tportAdapter
            viewModel.getTportSearchedPathList(binding.editTextOrgin.text.toString(), binding.editTextDestination.text.toString(), "6시 45분")
            viewModel.getTportSearchedPathList("경기도 화성시 청계동 KCC스위첸아파트", "서울역1호선", "6시 45분")
            viewModel.tportSearchedPathList.observe(this.viewLifecycleOwner) {
                viewModel.tportSearchedPathList.let { tportAdapter.submitList( it.value) }
            }
        }
*/
    }

    private fun navigateToDetail(path: Path, selectedTime: String) {
        val action =
            PathFindingFragmentDirections.actionPathFindingFragmentToPathDetailFragment(path, selectedTime)
        this.findNavController().navigate(action)
    }
}
