package com.example.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.preference.SettingPreference
import com.example.storyapp.ui.setting.ThemeViewModel
import com.example.storyapp.ui.splash.SplashViewModel

class ViewModelFactory(private val pref: SettingPreference) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(pref) as T
        }
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(pref) as T
        }

        throw IllegalAccessException("Unknown ViewModel class: " + modelClass.name)
    }

}