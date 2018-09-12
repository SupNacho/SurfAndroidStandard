package ru.surfstudio.android.location.sample.ui.screen.location_service_sample

import android.location.Location
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.exceptions.CompositeException
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency
import ru.surfstudio.android.core.ui.event.ScreenEventDelegateManager
import ru.surfstudio.android.core.ui.permission.PermissionManager
import ru.surfstudio.android.core.ui.provider.ActivityProvider
import ru.surfstudio.android.location.LocationService
import ru.surfstudio.android.location.domain.LocationPriority
import ru.surfstudio.android.location.location_errors_resolver.resolutions.LocationErrorResolution
import ru.surfstudio.android.location.location_errors_resolver.resolutions.impl.concrete.no_location_permission.NoLocationPermissionResolution
import ru.surfstudio.android.location.location_errors_resolver.resolutions.impl.concrete.play_services_are_not_available.PlayServicesAreNotAvailableResolution
import ru.surfstudio.android.location.location_errors_resolver.resolutions.impl.concrete.resolveble_api_exception.ResolvableApiExceptionResolution
import ru.surfstudio.android.location.sample.ui.screen.base.BaseSamplePresenter

/**
 * Презентер экрана [LocationServiceActivityView]
 */
class LocationServicePresenter(
        basePresenterDependency: BasePresenterDependency,
        screenEventDelegateManager: ScreenEventDelegateManager,
        permissionManager: PermissionManager,
        activityProvider: ActivityProvider,
        private val locationService: LocationService
) : BaseSamplePresenter<LocationServiceActivityView>(basePresenterDependency) {

    private val resolutions = mutableListOf<LocationErrorResolution<*>>()
    private val noLocationPermissionResolution =
            NoLocationPermissionResolution(permissionManager)
    private val playServicesAreNotAvailableResolution =
            PlayServicesAreNotAvailableResolution(screenEventDelegateManager, activityProvider)
    private val resolvableApiExceptionResolution =
            ResolvableApiExceptionResolution(screenEventDelegateManager, activityProvider)

    private var locationUpdatesDisposable: Disposable = Disposables.disposed()

    fun checkLocationAvailability() {
        view.showLoading()

        subscribeIo(
                locationService.observeLocationAvailability(LocationPriority.HIGH_ACCURACY),
                { hideLoadingAndShowLocationIsAvailable() },
                { t: Throwable -> hideLoadingAndShowLocationIsNotAvailable(t) }
        )
    }

    fun resolveLocationAvailability() {
        view.showLoading()

        val locationAvailabilityResolvingSingle =
                locationService.observeLocationAvailability(LocationPriority.HIGH_ACCURACY)
                        .toSingle { emptyList<Throwable>() }
                        .onErrorResumeNext { t: Throwable ->
                            if (t is CompositeException) {
                                locationService.observeLocationAvailabilityResolving(
                                        t.exceptions,
                                        *resolutions.toTypedArray()
                                )
                            } else {
                                Single.error(t)
                            }
                        }

        subscribeIo(
                locationAvailabilityResolvingSingle,
                { unresolvedExceptions ->
                    if (unresolvedExceptions.isEmpty()) {
                        hideLoadingAndShowLocationIsAvailable()
                    } else {
                        hideLoadingAndShowLocationIsNotAvailable(CompositeException(unresolvedExceptions))
                    }
                },
                { t: Throwable -> hideLoadingAndShowLocationIsNotAvailable(t) }
        )
    }

    fun getLastKnownLocation() {
        view.showLoading()

        subscribeIo(
                locationService.observeLastKnownLocation(),
                { location: Location -> hideLoadingAndShowLocation(location) },
                { hideLoadingAndShowNoLocation() },
                { t: Throwable -> hideLoadingAndShowLocationIsNotAvailable(t) }
        )
    }

    fun subscribeToLocationUpdates() {
        locationUpdatesDisposable = subscribeIo(
                locationService.observeLocationUpdates(1000, 1000, LocationPriority.HIGH_ACCURACY),
                { location: Location -> view.showLocation(location) },
                { /* do nothing */},
                { t: Throwable -> view.showLocationIsNotAvailable(t) }
        )
    }

    fun unsubscribeFromLocationUpdates() {
        locationUpdatesDisposable.dispose()
    }

    fun addNoLocationPermissionResolution() {
        resolutions.add(noLocationPermissionResolution)
    }

    fun removeNoLocationPermissionResolution() {
        resolutions.remove(noLocationPermissionResolution)
    }

    fun addPlayServicesAreNotAvailableResolution() {
        resolutions.add(playServicesAreNotAvailableResolution)
    }

    fun removePlayServicesAreNotAvailableResolution() {
        resolutions.remove(playServicesAreNotAvailableResolution)
    }

    fun addResolvableApiExceptionResolution() {
        resolutions.add(resolvableApiExceptionResolution)
    }

    fun removeResolvableApiExceptionResolution() {
        resolutions.remove(resolvableApiExceptionResolution)
    }
}