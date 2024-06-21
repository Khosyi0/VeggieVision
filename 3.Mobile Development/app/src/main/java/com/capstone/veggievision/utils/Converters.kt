package com.capstone.veggievision.utils

import androidx.room.TypeConverter
import com.capstone.veggievision.data.remote.response.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(sourceString: String): Source {
        val sourceType = object : TypeToken<Source>() {}.type
        return Gson().fromJson(sourceString, sourceType)
    }

    @TypeConverter
    fun fromAny(content: Any): String {
        return Gson().toJson(content)
    }

    @TypeConverter
    fun toAny(contentString: String): Any {
        val contentType = object : TypeToken<Any>() {}.type
        return Gson().fromJson(contentString, contentType)
    }
}