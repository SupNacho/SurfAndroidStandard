package ru.surfstudio.android.firebaseanalytics.api

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import ru.surfstudio.android.analyticsv2.core.AnalyticAction
import ru.surfstudio.android.analyticsv2.core.AnalyticActionPerformer

/**
 * Отправляет событие с именем и параметрами в Firebase аналитику
 */
class FirebaseAnalyticEventSender(private val firebaseAnalytics: FirebaseAnalytics) : AnalyticActionPerformer<FirebaseAnalyticEvent> {

    override fun canHandle(action: AnalyticAction) = action is FirebaseAnalyticEvent

    override fun perform(action: FirebaseAnalyticEvent) {
        val finalParams = cutParamsLength(action.params())
        firebaseAnalytics.logEvent(action.key().take(MAX_EVENT_KEY_LENGTH), finalParams)
    }

    /**
     * Обрезка параметров для выполнения ограничений FirebaseAnalytics
     */
    private fun cutParamsLength(params: Bundle?): Bundle? {
        if (params == null) {
            return params
        }
        val resultBundle = Bundle(params.size())
        params.let { bundle: Bundle ->
            bundle.keySet().forEach { key: String? ->
                key?.let {
                    val value = bundle.get(it)
                    if (value is String) {
                        resultBundle.putString(it.take(MAX_EVENT_KEY_LENGTH), bundle.getString(it)?.take(MAX_EVENT_VALUE_LENGTH))
                    } else {
                        throw Error("Non string value in bundle: $params. FireBase does not support that")
                    }
                }
            }
        }
        return resultBundle
    }
}