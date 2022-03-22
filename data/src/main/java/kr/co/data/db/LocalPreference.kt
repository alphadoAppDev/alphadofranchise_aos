package kr.co.data.db

import android.content.Context
import android.content.SharedPreferences

object LocalPreference  {
    private var instance: LocalPreference? = null
    private var localPref: SharedPreferences? = null
    private var localEditor: SharedPreferences.Editor? = null

    fun getInstance(): LocalPreference {
        return instance ?: synchronized(this) {
            instance ?: LocalPreference.also { instance = it }
        }
    }

    fun init(context: Context) {
        localPref = context.getSharedPreferences("local", Context.MODE_PRIVATE)
        localEditor = localPref!!.edit()
    }

    fun setFilter1(value: String, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putString("filter1", value).apply()
    }

    fun getFilter1(context: Context): String {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getString("filter1", "")!!
    }

    fun setFilter2(value: String, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putString("filter2", value).apply()
    }

    fun getFilter2(context: Context): String {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getString("filter2", "")!!
    }

    fun setFilter3(value: String, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putString("filter3", value).apply()
    }

    fun getFilter3(context: Context): String {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getString("filter3", "")!!
    }

    fun setFilter4(value: String, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putString("filter4", value).apply()
    }

    fun getFilter4(context: Context): String {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getString("filter4", "")!!
    }

    fun setFilter5(value: String, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putString("filter5", value).apply()
    }

    fun getFilter5(context: Context): String {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getString("filter5", "")!!
    }

    fun setIsFilterSet(value: Boolean, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putBoolean("isFilterSet", value).apply()
    }

    fun getIsFilterSet(context: Context): Boolean {
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getBoolean("isFilterSet", false)
    }

    fun setIsInitial(value : Int, context: Context){
        if(localEditor == null) {
            init(context)
        }
        localEditor!!.putInt("isInitial", value).apply()
    }

    fun getIsInitial(context: Context) : Int {
        val TYPE_NOR_SELECTED = 103
        if(localPref == null) {
            init(context)
        }
        return localPref!!.getInt("isInitial", TYPE_NOR_SELECTED)
    }
}