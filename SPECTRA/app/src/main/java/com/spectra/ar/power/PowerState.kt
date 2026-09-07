package com.spectra.ar.power

data class PowerState(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val isBatterySaver: Boolean = false,
    val isThermalThrottling: Boolean = false,
    val thermalStatus: Int = 0 // mirrors PowerManager.THERMAL_STATUS_* when available (API 29+)
) {
    val shouldReduceQuality: Boolean get() = isBatterySaver || isThermalThrottling || (batteryPercent < 20 && !isCharging)
}
