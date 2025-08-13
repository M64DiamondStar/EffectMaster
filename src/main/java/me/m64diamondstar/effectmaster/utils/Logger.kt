package me.m64diamondstar.effectmaster.utils

import me.m64diamondstar.effectmaster.EffectMaster

/**
 * Send a severe log to the console
 */
fun severeLog(log: String) {
    EffectMaster.plugin().logger.severe(log)
}

/**
 * Send a warning to the console
 */
fun warningLog(log: String) {
    EffectMaster.plugin().logger.warning(log)
}

/**
 * Send an informational log to the console
 */
fun infoLog(log: String) {
    EffectMaster.plugin().logger.info(log)
}