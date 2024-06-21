package com.capstone.veggievision.data.remote.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class NewsResponse(

    @field:SerializedName("totalResults")
    val totalResults: Int,

    @field:SerializedName("articles")
    val articles: List<ArticlesItem>,

    @field:SerializedName("status")
    val status: String
)

@Parcelize
data class Source(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: String?
) : Parcelable

@Parcelize
@Entity(tableName = "articles")
data class ArticlesItem(

    @field:PrimaryKey
    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("publishedAt")
    val publishedAt: String,

    @field:SerializedName("urlToImage")
    val urlToImage: String,

    @field:SerializedName("url")
    val url: String,

    @field:SerializedName("author")
    val author: String?,

    @field:SerializedName("description")
    val description: String?,

    @field:SerializedName("source")
    val source: @RawValue Source,

    @field:SerializedName("content")
    val content: @RawValue Any?
) : Parcelable