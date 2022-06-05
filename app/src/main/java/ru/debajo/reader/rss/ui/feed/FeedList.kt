package ru.debajo.reader.rss.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ru.debajo.reader.rss.R
import ru.debajo.reader.rss.ui.article.ChannelArticle
import ru.debajo.reader.rss.ui.article.model.UiArticle
import ru.debajo.reader.rss.ui.common.rememberEnterAlwaysScrollBehavior
import ru.debajo.reader.rss.ui.ext.plus
import ru.debajo.reader.rss.ui.feed.model.FeedListState
import ru.debajo.reader.rss.ui.host.ViewModels
import ru.debajo.reader.rss.ui.list.ScrollController
import ru.debajo.reader.rss.ui.main.MainScreenTopBarActions
import ru.debajo.reader.rss.ui.main.MainTopBar
import ru.debajo.reader.rss.ui.main.feedTab
import ru.debajo.reader.rss.ui.main.navigation.NavGraph
import ru.debajo.staggeredlazycolumn.StaggeredLazyColumn
import ru.debajo.staggeredlazycolumn.StaggeredLazyColumnCells
import ru.debajo.staggeredlazycolumn.state.StaggeredLazyColumnScrollState
import ru.debajo.staggeredlazycolumn.state.rememberStaggeredLazyColumnState

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedList(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    scrollController: ScrollController,
    viewModel: FeedListViewModel = ViewModels.feedListViewModel,
    forLandscape: Boolean = false,
    onArticleClick: (UiArticle) -> Unit,
) {
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopBar(
                tab = feedTab,
                scrollBehavior = scrollBehavior
            ) {
                val feedState by viewModel.state.collectAsState()
                if (feedState.showOnlyNewArticlesButtonVisible) {
                    MainScreenTopBarActions(feedState, viewModel)
                }
            }
        }
    ) { innerPaddingScaffold ->
        val state by viewModel.state.collectAsState()
        val isRefreshing by viewModel.isRefreshing.collectAsState()
        val listScrollState = scrollController.rememberLazyListState(NavGraph.Main.Feed.route)
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingScaffold),
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.onPullToRefresh() },
            indicator = { refreshState, trigger ->
                SwipeRefreshIndicator(
                    state = refreshState,
                    refreshTriggerDistance = trigger,
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            if (state.articles.isEmpty() && !isRefreshing) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Text(
                        text = stringResource(R.string.feed_is_empty),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 26.dp),
                    )
                }
            } else {
                ScrollToTopButton(
                    listScrollState = listScrollState,
                    contentPadding = innerPadding,
                ) {
                    ArticlesList(
                        listState = listScrollState,
                        forLandscape = forLandscape,
                        innerPadding = innerPadding,
                        state = state,
                        viewModel = viewModel,
                        onArticleClick = onArticleClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticlesList(
    forLandscape: Boolean,
    innerPadding: PaddingValues,
    listState: StaggeredLazyColumnScrollState = rememberStaggeredLazyColumnState(),
    state: FeedListState,
    viewModel: FeedListViewModel,
    onArticleClick: (UiArticle) -> Unit,
) {
    val cells = remember(forLandscape) {
        if (forLandscape) {
            StaggeredLazyColumnCells.Adaptive(200.dp, 3)
        } else {
            StaggeredLazyColumnCells.Fixed(1)
        }
    }

    StaggeredLazyColumn(
        modifier = Modifier.fillMaxSize(),
        columns = cells,
        state = listState,
        contentPadding = PaddingValues(
            top = 12.dp,
            bottom = 80.dp,
            start = 16.dp,
            end = 16.dp
        ) + innerPadding,
        verticalSpacing = 12.dp,
        content = {
            items(
                count = state.articles.size,
                key = { state.articles[it].id + state.articles[it].channelName },
                contentType = { "article" }
            ) { index ->
                ChannelArticle(
                    article = state.articles[index],
                    onFavoriteClick = { viewModel.onFavoriteClick(it) },
                    onView = { viewModel.onArticleViewed(it) },
                    onClick = onArticleClick
                )
            }
        }
    )
}

@Composable
fun ScrollToTopButton(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.scroll_to_top),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    listScrollState: StaggeredLazyColumnScrollState,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var canScrollToTop by remember { mutableStateOf(false) }
    LaunchedEffect(listScrollState) {
        combine(
            snapshotFlow { listScrollState.isScrollInProgress },
            snapshotFlow { listScrollState.scrollDirection }
        ) { _, b -> b }
            .collect {
                canScrollToTop = it == StaggeredLazyColumnScrollState.ScrollDirection.DOWN &&
                        listScrollState.canScroll(StaggeredLazyColumnScrollState.ScrollDirection.DOWN)
            }
    }
    Box(Modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .then(modifier),
            visible = canScrollToTop
        ) {
            Button(
                modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding() + 16.dp),
                onClick = {
                    coroutineScope.launch {
                        listScrollState.animateScrollToItem(0)
                    }
                }
            ) {
                Text(text)
            }
        }
    }
}
