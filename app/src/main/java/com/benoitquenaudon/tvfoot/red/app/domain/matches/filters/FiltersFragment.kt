package com.benoitquenaudon.tvfoot.red.app.domain.matches.filters


import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benoitquenaudon.rxdatabinding.databinding.RxObservableBoolean
import com.benoitquenaudon.tvfoot.red.R
import com.benoitquenaudon.tvfoot.red.app.common.BaseFragment
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesIntent
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesIntent.ClearFilters
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesIntent.FilterInitialIntent
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesIntent.ToggleFilterIntent
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesViewModel
import com.benoitquenaudon.tvfoot.red.app.domain.matches.state.MatchesViewState
import com.benoitquenaudon.tvfoot.red.app.mvi.MviView
import com.benoitquenaudon.tvfoot.red.databinding.FragmentFiltersBinding
import com.jakewharton.rxbinding2.support.v7.widget.RxToolbar
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class FiltersFragment : BaseFragment(), MviView<MatchesIntent, MatchesViewState> {
  @Inject lateinit var disposables: CompositeDisposable
  @Inject lateinit var bindingModel: FiltersBindingModel
  @Inject lateinit var stateBinder: MatchesViewModel
  @Inject lateinit var filtersAdapter: FiltersAdapter

  lateinit var binding: FragmentFiltersBinding

  companion object Factory {
    fun newInstance(): FiltersFragment = FiltersFragment()
  }

  @Suppress("OverridingDeprecatedMember", "DEPRECATION")
  override fun onAttach(activity: Activity?) {
    fragmentComponent.inject(this)
    super.onAttach(activity)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate<FragmentFiltersBinding>(inflater, R.layout.fragment_filters,
        container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.filtersToolbar.inflateMenu(R.menu.fragment_filters)

    binding.filtersList.apply {
      addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
      adapter = filtersAdapter
    }

    bind()
  }

  override fun onDestroyView() {
    disposables.dispose()
    super.onDestroyView()
  }

  override fun intents(): Observable<MatchesIntent> {
    return Observable.merge(initialIntent(), clearFilterIntent(), filterClickIntent())
  }

  private fun initialIntent(): Observable<FilterInitialIntent> =
      Observable.just(FilterInitialIntent)

  private fun clearFilterIntent(): Observable<ClearFilters> =
      RxToolbar.itemClicks(binding.filtersToolbar)
          .filter { it.itemId == R.id.action_clear }.map { ClearFilters }

  private fun filterClickIntent(): Observable<ToggleFilterIntent> =
      filtersAdapter.filterRowClickObservable.map { ToggleFilterIntent(it.code) }

  override fun render(state: MatchesViewState) {
    bindingModel.updateFromState(state)
  }

  private fun bind() {
    disposables.add(stateBinder.states().subscribe(this::render))
    stateBinder.processIntents(intents())
    disposables.add(
        RxObservableBoolean.propertyChanges(bindingModel.hasFilters)
            .startWith(bindingModel.hasFilters.get()) // fix for rotation
            .subscribe {
              binding.filtersToolbar.menu.findItem(R.id.action_clear).isVisible = it
            }
    )
  }
}
