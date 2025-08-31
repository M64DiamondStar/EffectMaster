package me.m64diamondstar.effectmaster.ktx

import me.m64diamondstar.effectmaster.EffectMaster

fun sync(task: Runnable) {
    EffectMaster.getFoliaLib().scheduler.runNextTick{
        task.run()
    }
}