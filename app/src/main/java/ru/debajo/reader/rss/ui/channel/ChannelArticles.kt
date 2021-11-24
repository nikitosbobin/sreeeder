package ru.debajo.reader.rss.ui.channel

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ru.debajo.reader.rss.di.diViewModel
import ru.debajo.reader.rss.ui.channels.model.UiChannel
import timber.log.Timber

const val ChannelArticlesRoute = "ChannelArticles"
private const val ChannelArticlesRouteChannelParam = "ChannelId"

fun channelArticlesRouteParams(channel: UiChannel): Bundle {
    return bundleOf(ChannelArticlesRouteChannelParam to channel)
}

fun extractUiChannel(bundle: Bundle?): UiChannel {
    return bundle?.getParcelable(ChannelArticlesRouteChannelParam)!!
}

@ExperimentalMaterial3Api
@Composable
fun ChannelArticles(channel: UiChannel) {
    val viewModel = diViewModel<ChannelArticlesViewModel>()
    LaunchedEffect(key1 = channel, block = {
        viewModel.load(channel)
    })
    Scaffold {
        val articles by viewModel.articles.collectAsState()
        if (articles != null) {
            RenderHtml(articles!!)
        }
    }
}

@Composable
fun RenderHtml(articles: Document) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        for (child in articles.body().children()) {
            RenderBlock(child)
        }
    }
}

@Composable
fun RenderBlock(block: Element) {
    val name = block.tag().name
    Timber.d("yopta $name, childrenCount: ${block.childrenSize()}")
    when (name) {
        "head" -> RenderHtmlHeadTag(block)
        "p" -> RenderHtmlPTag(block)
        "h1" -> RenderHtmlHeadingTag(block, 1)
        "h2" -> RenderHtmlHeadingTag(block, 2)
        "h3" -> RenderHtmlHeadingTag(block, 3)
        "h4" -> RenderHtmlHeadingTag(block, 4)
        "h5" -> RenderHtmlHeadingTag(block, 5)
        "h6" -> RenderHtmlHeadingTag(block, 6)
        "ul" -> RenderHtmlUlTag(block)
        else -> Timber.d("yopta unknown tag: $name")
    }
}

@Composable
fun RenderHtmlUlTag(block: Element) {
    Column {
        for (li in block.children().filter { it.tagName() == "li" }) {
            RenderHtmlLiTag(li)
        }
    }
}

@Composable
fun RenderHtmlLiTag(li: Element) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LocalContentColor.current)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(li.ownText())
    }
}

@Composable
fun RenderHtmlHeadingTag(heading: Element, headingFactor: Int) {
    val style = when (headingFactor) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        3 -> MaterialTheme.typography.headlineSmall
        4 -> MaterialTheme.typography.titleLarge
        5 -> MaterialTheme.typography.titleMedium
        6 -> MaterialTheme.typography.titleSmall
        else -> return
    }
    Text(
        text = heading.ownText(),
        style = style,
    )
}

@Composable
fun RenderHtmlPTag(p: Element) {
    Box(Modifier.padding(vertical = 16.dp)) {
        val nodes = p.childNodes() // тут надо что то придумать

        Text(p.wholeText())
    }
}

@Composable
fun RenderHtmlHeadTag(head: Element) {
    Column {
        for (child in head.children()) {
            RenderBlock(child)
        }
    }
}
