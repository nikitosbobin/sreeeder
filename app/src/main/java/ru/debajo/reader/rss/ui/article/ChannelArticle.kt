package ru.debajo.reader.rss.ui.article

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import org.joda.time.DateTime
import org.joda.time.LocalDate
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import ru.debajo.reader.rss.ui.common.AppCard

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LazyItemScope.ChannelArticle(
    article: UiArticle,
    channel: UiChannel? = null,
    onFavoriteClick: (UiArticle) -> Unit,
    onClick: (UiArticle) -> Unit,
) {
    AppCard(
        onClick = { onClick(article) },
        modifier = Modifier.animateItemPlacement().fillMaxWidth()
    ) {
        Column {
            if (article.image != null) {
                Image(
                    painter = rememberImagePainter(article.image, builder = {
                        size(OriginalSize)
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            RoundedCornerShape(18.dp)
                        )
                        .clip(RoundedCornerShape(18.dp)),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(article.title, Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            article.timestamp?.let {
                Text(it.format(), Modifier.padding(horizontal = 16.dp), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }
            channel?.let {
                ChannelBar(channel)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    modifier = Modifier.clickable { onFavoriteClick(article) },
                    imageVector = if (article.bookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ChannelBar(channel: UiChannel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (channel.image != null) {
            Image(
                painter = rememberImagePainter(channel.image),
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(channel.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun DateTime.format(): String {
    val now = LocalDate.now()

    return when {
        toLocalDate() == now -> stringResource(R.string.article_date_time_today, toString("HH:mm"))

        toLocalDate() == now.minusDays(1) -> stringResource(R.string.article_date_time_yesterday, toString("HH:mm"))

        else -> toString("dd MMMM yyyy HH:mm")
    }
}