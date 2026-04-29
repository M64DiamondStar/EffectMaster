package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.EffectShow

class InvalidParameterException(id: Int, effectShow: EffectShow, message: String): Exception("Couldn't play ParticleEmitter with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}. " + message)