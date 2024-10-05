package com.example.weatherapp.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.weatherapp.Constant

import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.databinding.SettingBinding


class Setting : Fragment() {
    private var _binding: SettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationSharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = SettingBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            Constant.SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )
        locationSharedPreferences = requireActivity().getSharedPreferences(
            Constant.LOCATION_SHARED_PREFERENCE,
            Context.MODE_PRIVATE)
            updateData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.show()
        binding.radiolanid.setOnCheckedChangeListener { group, checkedId ->
            val languageRadioButton: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (languageRadioButton.text) {

                getString(R.string.arabic) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constant.LANGUAGE_KEY,
                            Constant.Enum_lANGUAGE.ar.toString()
                        )
                        .apply()
                    changeLanguageLocaleTo("ar")
                }


                getString(R.string.english) ->

                {
                    sharedPreferences.edit()
                        .putString(Constant.LANGUAGE_KEY, Constant.Enum_lANGUAGE.en.toString())
                        .apply()
                    changeLanguageLocaleTo("en")
                }
            }
        }
        binding.radiotempid.setOnCheckedChangeListener { group, checkedId ->
            val temperatureRadioButton: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (temperatureRadioButton.text) {
                getString(R.string.celsius) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constant.UNITS_KEY,
                            Constant.ENUM_UNITS.metric.toString()
                        )
                        .apply()
                }
                getString(R.string.kelvin) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constant.UNITS_KEY,
                            Constant.ENUM_UNITS.standard.toString()
                        )
                        .apply()
                }
                getString(R.string.fahrenheit) ->{
                    sharedPreferences.edit()
                        .putString(
                            Constant.UNITS_KEY,
                            Constant.ENUM_UNITS.imperial.toString()
                        )
                        .apply()
                }
            }
        }

        binding.radiolocaationid.setOnCheckedChangeListener{group, checkedId ->
            val locationRadioGroup: RadioButton = binding.root.findViewById(checkedId) as RadioButton
            when (locationRadioGroup.text) {

                getString(R.string.gps) -> {
                    locationSharedPreferences.edit()
                        .putString(
                            Constant.LOCATION_KEY,
                            Constant.ENUM_LOCATION.gps.toString()
                        ).apply()
                }

                getString(R.string.map) -> {
                    locationSharedPreferences.edit()
                        .putString(
                            Constant.LOCATION_KEY,
                            Constant.ENUM_LOCATION.map.toString()
                        ).apply()
                    var tye = "Setting"
                    var action : SettingDirections.ActionNavSettingToMapsFragment =
                        SettingDirections.actionNavSettingToMapsFragment().apply {
                            type = tye
                        }
                    Navigation.findNavController(requireView()).navigate(action)

                }
            }
        }
    }

    private fun changeLanguageLocaleTo(languageTag: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun updateData() {
        var lang = sharedPreferences.getString(
            Constant.LANGUAGE_KEY,
            Constant.Enum_lANGUAGE.en.toString()
        )

        var unit = sharedPreferences.getString(
            Constant.UNITS_KEY,
            Constant.ENUM_UNITS.metric.toString()
        )

        var location = locationSharedPreferences.getString(
            Constant.LOCATION_KEY,
            Constant.ENUM_LOCATION.gps.toString()
        )
        Log.i("SettingFragment", "Location method selected: $location")

        if (lang == Constant.Enum_lANGUAGE.en.toString()) {
            binding.radiolanid.check(binding.englishbtn.id)
        } else {
            binding.radiolanid.check(binding.arabicbtn.id)
        }
        //==================

        if (unit == Constant.ENUM_UNITS.standard.toString()){
            binding.radiotempid.check(binding.kelvinid.id)}

        else if (unit == Constant.ENUM_UNITS.imperial.toString()){
            binding.radiotempid.check(binding.fahrenheitid.id)}

        else{
            binding.radiotempid.check(binding.celisid.id)}
        //-----------------------

        if (location == Constant.ENUM_LOCATION.gps.toString()) {
            binding.radiolocaationid.check(binding.gpsid.id)
        } else {
            binding.radiolocaationid.check(binding.mapid.id)
        }


    }
    }



