package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.type.*
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

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
        SET_BLOCK {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = SetBlock(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.STONE
        },
        FILL_BLOCK {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = FillBlock(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.DEEPSLATE
        },
        REPLACE_FILL {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ReplaceFill(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.GRANITE
        },
        FOUNTAIN {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Fountain(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.WATER_BUCKET
        },
        FOUNTAIN_LINE{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = FountainLine(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.LIGHT_BLUE_CONCRETE
        },
        FOUNTAIN_PATH {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = FountainPath(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.BLUE_CONCRETE
        },
        ITEM_FOUNTAIN{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ItemFountain(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.SPLASH_POTION
        },
        ITEM_FOUNTAIN_LINE{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ItemFountainLine(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.LINGERING_POTION
        },
        ITEM_FOUNTAIN_PATH {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ItemFountainPath(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.TIPPED_ARROW
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
        PARTICLE_PATH {
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ParticlePath(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.COMPARATOR
        },
        ACTIVATOR{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Activator(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.REDSTONE_TORCH
        },
        CONSOLE_COMMAND{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = ConsoleCommand(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.COMMAND_BLOCK
        },
        FIREWORK{
            override fun getTypeClass(effectShow: EffectShow, id: Int): Effect = Firework(effectShow, id)
            override fun getDisplayMaterial(): Material = Material.FIREWORK_ROCKET
        };

        abstract fun getTypeClass(effectShow: EffectShow, id: Int): Effect

        abstract fun getDisplayMaterial(): Material
    }

    /**
     * @param players The list of players for whom the show needs to be visible. If this is null, the show will play for everyone.
     */
    abstract fun execute(players: List<Player>?)

    abstract fun getType(): Type

    abstract fun isSync(): Boolean

    abstract fun getDefaults(): List<Pair<String, Any>>


    fun getID(): Int = id

    fun getDelay(): Long = getSection().getLong("Delay")

    fun getSection(): ConfigurationSection = effectShow.getConfig().getConfigurationSection("$id")!!

    fun getShow(): EffectShow = effectShow

}