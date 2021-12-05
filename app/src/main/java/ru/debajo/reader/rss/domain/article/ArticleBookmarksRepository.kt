package ru.debajo.reader.rss.domain.article

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.debajo.reader.rss.data.db.dao.ArticleBookmarksDao
import ru.debajo.reader.rss.data.db.model.DbArticleBookmark
import ru.debajo.reader.rss.data.db.model.DbDateTime
import ru.debajo.reader.rss.metrics.Analytics

class ArticleBookmarksRepository(
    private val articleBookmarksDao: ArticleBookmarksDao,
    private val analytics: Analytics,
) {
    fun observe(): Flow<List<String>> {
        return articleBookmarksDao.getAll()
            .map { list -> list.map { it.articleId } }
            .flowOn(IO)
    }

    suspend fun toggle(articleId: String) {
        withContext(IO) {
            val saved = articleBookmarksDao.getById(articleId) != null
            if (saved) {
                articleBookmarksDao.remove(articleId)
                analytics.onRemoveBookmark()
            } else {
                articleBookmarksDao.add(DbArticleBookmark(articleId, DbDateTime.now()))
                analytics.onBookmark()
            }
        }
    }
}
