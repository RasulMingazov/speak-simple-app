package org.speaksimpleapp.feature.auth.di

import android.app.Activity
import org.speaksimpleapp.feature.auth.data.platform.AndroidActivityProvider

interface AndroidAuthContainer : AuthContainer {
    fun attachActivity(activity: Activity)
    fun detachActivity(activity: Activity)
}

internal class DefaultAndroidAuthContainer(
    private val delegate: AuthContainer,
    private val activityProvider: AndroidActivityProvider,
) : AndroidAuthContainer, AuthContainer by delegate {
    override fun attachActivity(activity: Activity) = activityProvider.attach(activity)

    override fun detachActivity(activity: Activity) = activityProvider.detach(activity)
}
