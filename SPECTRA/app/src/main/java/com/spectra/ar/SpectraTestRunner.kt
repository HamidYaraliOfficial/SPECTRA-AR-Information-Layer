package com.spectra.ar

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/** Swaps in Hilt's test application for instrumented tests so @HiltAndroidTest classes
 *  can inject real (or test-double) dependencies from the same DI graph as the app. */
class SpectraTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application =
        super.newApplication(cl, HiltTestApplication::class.java.name, context)
}
