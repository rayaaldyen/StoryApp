package com.example.storyapp.preference

import android.content.Context
import com.example.storyapp.data.remote.user.LoginModel

class UserPreference(context: Context) {

    private val preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_pref"
        private const val NAME = "name"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
    }

    fun getUser(): LoginModel {
        val name = preference.getString(NAME, null)
        val userId = preference.getString(USER_ID, null)
        val token = preference.getString(TOKEN, null)
        return LoginModel(name, userId, token)
    }

    fun saveUser(user: LoginModel) {
        val edit = preference.edit()
        edit.putString(NAME, user.name)
        edit.putString(USER_ID, user.userId)
        edit.putString(TOKEN, user.token)
        edit.apply()
    }


    fun logout() {
        val edit = preference.edit().clear()
        edit.apply()
    }


}