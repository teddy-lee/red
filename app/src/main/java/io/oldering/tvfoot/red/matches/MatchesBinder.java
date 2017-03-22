package io.oldering.tvfoot.red.matches;

import android.app.Activity;
import android.support.annotation.VisibleForTesting;
import io.oldering.tvfoot.red.di.ActivityScope;
import io.oldering.tvfoot.red.util.schedulers.BaseSchedulerProvider;
import io.reactivex.Observable;
import javax.inject.Inject;

@ActivityScope class MatchesBinder {
  private final MatchesActivity activity;
  private final MatchesInteractor repository;
  private final BaseSchedulerProvider schedulerProvider;

  @Inject MatchesBinder(Activity activity, MatchesInteractor repository,
      BaseSchedulerProvider schedulerProvider) {
    this.activity = (MatchesActivity) activity;
    this.repository = repository;
    this.schedulerProvider = schedulerProvider;
  }

  @VisibleForTesting Observable<MatchesIntent> intent() {
    return Observable.merge(activity.loadFirstPageIntent(), activity.loadNextPageIntent(),
        activity.matchRowClickIntent()).subscribeOn(schedulerProvider.ui());
  }

  @VisibleForTesting Observable<MatchesViewState> model(Observable<MatchesIntent> intents) {
    return intents.flatMap(intent -> {
      if (intent instanceof MatchesIntent.LoadFirstPage) {
        return repository.loadFirstPage().subscribeOn(schedulerProvider.io());
      }
      if (intent instanceof MatchesIntent.LoadNextPage) {
        return repository.loadNextPage(((MatchesIntent.LoadNextPage) intent).currentPage())
            .subscribeOn(schedulerProvider.io());
      }
      if (intent instanceof MatchesIntent.MatchRowClick) {
        return Observable.just(
            MatchesViewState.matchRowClick(((MatchesIntent.MatchRowClick) intent).getMatch()));
      }
      throw new IllegalArgumentException("I don't know how to deal with this intent " + intent);
    }).scan(MatchesViewState::reduce).subscribeOn(schedulerProvider.computation());
  }

  @VisibleForTesting void view(Observable<MatchesViewState> states) {
    states.observeOn(schedulerProvider.ui()).subscribe(activity::render);
  }

  void bind() {
    this.view(this.model(this.intent()));
  }
}
