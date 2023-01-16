package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.type.*
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

abstract class Effect(private val show: Show, private val id: Int) {

    enum class Type {
        ANIMATRONIC {
            override fun getTypeClass(show: Show, id: Int): Effect = Animatronic(show, id)
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        ANIMATRONIC_GROUP {
            override fun getTypeClass(show: Show, id: Int): Effect = AnimatronicGroup(show, id)
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        FALLING_BLOCK {
            override fun getTypeClass(show: Show, id: Int): Effect = FallingBlock(show, id)
            override fun getDisplayMaterial(): Material = Material.SAND
        },
        FILL_BLOCK {
            override fun getTypeClass(show: Show, id: Int): Effect = FillBlock(show, id)
            override fun getDisplayMaterial(): Material = Material.DEEPSLATE
        },
        FOUNTAIN {
            override fun getTypeClass(show: Show, id: Int): Effect = Fountain(show, id)
            override fun getDisplayMaterial(): Material = Material.WATER_BUCKET
        },
        PARTICLE {
            override fun getTypeClass(show: Show, id: Int): Effect = Particle(show, id)
            override fun getDisplayMaterial(): Material = Material.GLOWSTONE_DUST
        },
        PARTICLE_EMITTER {
            override fun getTypeClass(show: Show, id: Int): Effect = ParticleEmitter(show, id)
            override fun getDisplayMaterial(): Material = Material.DISPENSER
        },
        PARTICLE_LINE {
            override fun getTypeClass(show: Show, id: Int): Effect = ParticleLine(show, id)
            override fun getDisplayMaterial(): Material = Material.REPEATER
        },
        SET_BLOCK {
            override fun getTypeClass(show: Show, id: Int): Effect = SetBlock(show, id)
            override fun getDisplayMaterial(): Material = Material.STONE
        },
        ACTIVATOR{
            override fun getTypeClass(show: Show, id: Int): Effect = Activator(show, id)
            override fun getDisplayMaterial(): Material = Material.REDSTONE_TORCH
        };

        abstract fun getTypeClass(show: Show, id: Int): Effect

        abstract fun getDisplayMaterial(): Material
    }

    abstract fun execute()

    abstract fun getType(): Type

    abstract fun isSync(): Boolean

    abstract fun getDefaults(): List<Pair<String, Any>>


    fun getID(): Int = id

    fun getDelay(): Long = getSection().getLong("Delay")

    fun getSection(): ConfigurationSection = show.getConfig().getConfigurationSection("$id")!!

    fun getShow(): Show = show

}