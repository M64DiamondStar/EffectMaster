package me.m64diamondstar.effectmaster.ktx

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object PluginScope : CoroutineScope by CoroutineScope(Dispatchers.IO)