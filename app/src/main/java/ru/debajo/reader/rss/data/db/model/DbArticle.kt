package ru.debajo.reader.rss.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbArticle(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "channelUrl")
    val channelUrl: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "descriptionHtml")
    val descriptionHtml: String,

    @ColumnInfo(name = "contentHtml")
    val contentHtml: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: DbDateTime
)