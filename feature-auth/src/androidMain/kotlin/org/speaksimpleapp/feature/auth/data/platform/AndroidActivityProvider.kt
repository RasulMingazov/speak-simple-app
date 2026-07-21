package org.speaksimpleapp.feature.auth.data.platform

import android.app.Activity
import org.speaksimpleapp.feature.auth.data.exception.AuthDataException
import java.lang.ref.WeakReference

internal class AndroidActivityProvider {
    private val lock = Any()
    private var activityReference: WeakReference<Activity>? = null

    fun attach(activity: Activity) = synchronized(lock) {
        activityReference = WeakReference(activity)
    }

    fun detach(activity: Activity) = synchronized(lock) {
        val reference = activityReference
        if (reference?.get() === activity) {
            reference.clear()
            activityReference = null
        }
    }

    internal fun requireActivity(): Activity = synchronized(lock) {
        activityReference?.get()
    } ?: throw AuthDataException()
}
