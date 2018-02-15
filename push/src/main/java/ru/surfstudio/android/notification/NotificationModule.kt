package ru.surfstudio.android.notification

import dagger.Module
import dagger.Provides
import ru.surfstudio.android.notification.ui.notification.AbstractPushHandleStrategyFactory
import ru.surfstudio.android.core.util.ActiveActivityHolder

/**
 * Dagger-модуль для удовлетворения зависимостей
 */
@Module
class NotificationModule(
        private val activeActivityHolder: ActiveActivityHolder,
        private val pushHandleStrategyFactory: AbstractPushHandleStrategyFactory
) {

    @Provides
    fun provideActiveActivityHolder(): ActiveActivityHolder = activeActivityHolder

    @Provides
    fun providePushHanleFactory(): AbstractPushHandleStrategyFactory = pushHandleStrategyFactory

}