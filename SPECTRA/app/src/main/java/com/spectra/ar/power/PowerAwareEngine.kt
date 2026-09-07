package com.spectra.ar.power

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Watches Android's Battery and Thermal APIs and exposes a single [PowerState] that every
 * downstream system (BackpressureController, ObjectDetector, ArSessionManager…) reads to
 * scale quality down gracefully instead of draining the battery or overheating the device.
 */
@Singleton
class PowerAwareEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _currentState = MutableStateFlow(PowerState())
    val currentState: StateFlow<PowerState> = _currentState.asStateFlow()

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else _currentState.value.batteryPercent
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            update { it.copy(batteryPercent = percent, isCharging = charging, isBatterySaver = powerManager?.isPowerSaveMode == true) }
        }
    }

    fun start() {
        context.registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            powerManager?.addThermalStatusListener { status ->
                update { it.copy(isThermalThrottling = status >= PowerManager.THERMAL_STATUS_MODERATE, thermalStatus = status) }
            }
        }
    }

    fun stop() {
        runCatching { context.unregisterReceiver(batteryReceiver) }
    }

    private inline fun update(transform: (PowerState) -> PowerState) {
        _currentState.value = transform(_currentState.value)
    }
}
