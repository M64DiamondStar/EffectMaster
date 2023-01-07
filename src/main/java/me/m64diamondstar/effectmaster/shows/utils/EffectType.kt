package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.type.*
import org.bukkit.configuration.ConfigurationSection

abstract class EffectType(private val show: Show, private val id: Int) {

    enum class Types {
        ANIMATRONIC {
            override fun getTypeClass(show: Show, id: Int): EffectType = Animatronic(show, id)
        },
        ANIMATRONIC_GROUP {
            override fun getTypeClass(show: Show, id: Int): EffectType = AnimatronicGroup(show, id)
        },
        FALLING_BLOCK {
            override fun getTypeClass(show: Show, id: Int): EffectType = FallingBlock(show, id)
        },
        FILL_BLOCK {
            override fun getTypeClass(show: Show, id: Int): EffectType = FillBlock(show, id)
        },
        FOUNTAIN {
            override fun getTypeClass(show: Show, id: Int): EffectType = Fountain(show, id)
        },
        PARTICLE {
            override fun getTypeClass(show: Show, id: Int): EffectType = Particle(show, id)
        },
        PARTICLE_EMITTER {
            override fun getTypeClass(show: Show, id: Int): EffectType = ParticleEmitter(show, id)
        },
        PARTICLE_LINE {
            override fun getTypeClass(show: Show, id: Int): EffectType = ParticleLine(show, id)
        },
        SET_BLOCK {
            override fun getTypeClass(show: Show, id: Int): EffectType = SetBlock(show, id)
        },
        ACTIVATOR{
            override fun getTypeClass(show: Show, id: Int): EffectType = Activator(show, id)
        };

        abstract fun getTypeClass(show: Show, id: Int): EffectType
    }

    abstract fun execute()

    abstract fun getType(): Types

    abstract fun isSync(): Boolean

    fun getSection(): ConfigurationSection {
        return show.getConfig().getConfigurationSection("$id")!!
    }

    fun getShow(): Show {
        return show
    }

}