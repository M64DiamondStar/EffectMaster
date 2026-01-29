package me.m64diamondstar.effectmaster.ktx

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.Bukkit

fun sync(task: Runnable) {
    Bukkit.getGlobalRegionScheduler().run(EffectMaster.plugin()) {
        task.run()
    }
}