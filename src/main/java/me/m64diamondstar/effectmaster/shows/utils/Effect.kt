package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.type.*
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

abstract class Effect(private val effectShow: EffectShow, private val id: Int) {

    enum class Type {
        ANIMATRONIC {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Animatronic(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        ANIMATRONIC_GROUP {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = AnimatronicGroup(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        FALLING_BLOCK {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = FallingBlock(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.SAND
        },
        FILL_BLOCK {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = FillBlock(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.DEEPSLATE
        },
        FOUNTAIN {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Fountain(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.WATER_BUCKET
        },
        PARTICLE {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Particle(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.GLOWSTONE_DUST
        },
        PARTICLE_EMITTER {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ParticleEmitter(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.DISPENSER
        },
        PARTICLE_LINE {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ParticleLine(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.REPEATER
        },
        SET_BLOCK {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = SetBlock(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.STONE
        },
        ACTIVATOR{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Activator(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.REDSTONE_TORCH
        };

        abstract fun getTypeClass(effectShow: EffectShow, id: Int): Effect

        abstract fun getDisplayMaterial(): Material
    }

    abstract fun execute()

    abstract fun getType(): Type

    abstract fun isSync(): Boolean

    abstract fun getDefaults(): List<Pair<String, Any>>


    fun getID(): Int = id

    fun getDelay(): Long = getSection().getLong("Delay")

    fun getSection(): ConfigurationSection = effectShow.getConfig().getConfigurationSection("$id")!!

    fun getShow(): EffectShow = effectShow

}