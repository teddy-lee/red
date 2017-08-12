package com.benoitquenaudon.tvfoot.red.app.data.source

import com.benoitquenaudon.tvfoot.red.app.data.entity.Match
import io.reactivex.Single

interface BaseMatchesRepository {
  fun loadPage(pageIndex: Int): Single<List<Match>>
}