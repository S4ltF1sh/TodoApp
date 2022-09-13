package com.example.todo.bottomSheets

import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.example.todo.databinding.BottomSheetDateAndTimePickerBinding
import com.example.todo.utils.TimeUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class DateAndTimePickerBottomSheet(private val setNewTime: (Date?) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDateAndTimePickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDateAndTimePickerBinding.inflate(inflater, container, false)
        binding.timePicker.setIs24HourView(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        notifyDetailTimeChange()
        setListener()
        setOnClick()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClick() {
        binding.apply {
            btnCancel.setOnClickListener {
                cancelButtonClicked()
            }

            btnOK.setOnClickListener {
                okButtonClicked()
            }
        }
    }

    private fun setListener() {
        binding.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                datePicker.setOnDateChangedListener(
                    DatePicker.OnDateChangedListener(
                        onDateChangedListener
                    )
                )
            }

            timePicker.setOnTimeChangedListener(onTimeChangeListener)
        }
    }

    private val onDateChangedListener = { _: DatePicker, _: Int, _: Int, _: Int ->
        notifyDetailTimeChange()

    }

    private val onTimeChangeListener = { _: TimePicker, _: Int, _: Int ->
        notifyDetailTimeChange()
    }

    private fun notifyDetailTimeChange() {
        val date = getDate()
        val dateString = TimeUtil.format(date)
        binding.tvDetailTime.text = dateString
        binding.btnOK.isEnabled = date != null && date > TimeUtil.currentTime()
    }

    private fun cancelButtonClicked() = this.dismiss()

    private fun okButtonClicked() {
        setNewTime(getDate())
        Toast.makeText(context, "Nhắc nhở vào ${binding.tvDetailTime.text}", Toast.LENGTH_LONG)
            .show()

        cancelButtonClicked()
    }

    private fun getDate(): Date? {
        binding.apply {
            val year = datePicker.year
            val month = datePicker.month
            val dayOfMonth = datePicker.dayOfMonth

            val hour = timePicker.hour
            val minute = timePicker.minute

            val date = GregorianCalendar(year, month, dayOfMonth, hour, minute)
            return date.time
        }
    }
}