package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.type.*
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class Effect() {

    enum class Type {
        ANIMATRONIC {
            override fun getTypeClass(): Effect = Animatronic()
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        ANIMATRONIC_GROUP {
            override fun getTypeClass(): Effect = AnimatronicGroup()
            override fun getDisplayMaterial(): Material = Material.ARMOR_STAND
        },
        FALLING_BLOCK {
            override fun getTypeClass(): Effect = FallingBlock()
            override fun getDisplayMaterial(): Material = Material.SAND
        },
        SET_BLOCK {
            override fun getTypeClass(): Effect = SetBlock()
            override fun getDisplayMaterial(): Material = Material.STONE
        },
        FILL_BLOCK {
            override fun getTypeClass(): Effect = FillBlock()
            override fun getDisplayMaterial(): Material = Material.POLISHED_ANDESITE
        },
        REPLACE_FILL {
            override fun getTypeClass(): Effect = ReplaceFill()
            override fun getDisplayMaterial(): Material = Material.GRANITE
        },
        BLOCK_LINE {
            override fun getTypeClass(): Effect = BlockLine()
            override fun getDisplayMaterial(): Material = Material.CHISELED_STONE_BRICKS
        },
        BLOCK_PATH {
            override fun getTypeClass(): Effect = BlockPath()
            override fun getDisplayMaterial(): Material = Material.SMOOTH_STONE
        },
        FOUNTAIN {
            override fun getTypeClass(): Effect = Fountain()
            override fun getDisplayMaterial(): Material = Material.WATER_BUCKET
        },
        FOUNTAIN_LINE{
            override fun getTypeClass(): Effect = FountainLine()
            override fun getDisplayMaterial(): Material = Material.LIGHT_BLUE_CONCRETE
        },
        FOUNTAIN_PATH {
            override fun getTypeClass(): Effect = FountainPath()
            override fun getDisplayMaterial(): Material = Material.BLUE_CONCRETE
        },
        ITEM_FOUNTAIN{
            override fun getTypeClass(): Effect = ItemFountain()
            override fun getDisplayMaterial(): Material = Material.SPLASH_POTION
        },
        ITEM_FOUNTAIN_LINE{
            override fun getTypeClass(): Effect = ItemFountainLine()
            override fun getDisplayMaterial(): Material = Material.LINGERING_POTION
        },
        ITEM_FOUNTAIN_PATH {
            override fun getTypeClass(): Effect = ItemFountainPath()
            override fun getDisplayMaterial(): Material = Material.TIPPED_ARROW
        },
        PARTICLE {
            override fun getTypeClass(): Effect = Particle()
            override fun getDisplayMaterial(): Material = Material.GLOWSTONE_DUST
        },
        PARTICLE_EMITTER {
            override fun getTypeClass(): Effect = ParticleEmitter()
            override fun getDisplayMaterial(): Material = Material.DISPENSER
        },
        PARTICLE_LINE {
            override fun getTypeClass(): Effect = ParticleLine()
            override fun getDisplayMaterial(): Material = Material.REPEATER
        },
        PARTICLE_PATH {
            override fun getTypeClass(): Effect = ParticlePath()
            override fun getDisplayMaterial(): Material = Material.COMPARATOR
        },
        ACTIVATOR{
            override fun getTypeClass(): Effect = Activator()
            override fun getDisplayMaterial(): Material = Material.REDSTONE_TORCH
        },
        CONSOLE_COMMAND{
            override fun getTypeClass(): Effect = ConsoleCommand()
            override fun getDisplayMaterial(): Material = Material.COMMAND_BLOCK
        },
        FIREWORK{
            override fun getTypeClass(): Effect = Firework()
            override fun getDisplayMaterial(): Material = Material.FIREWORK_ROCKET
        },
        PLAY_SHOW{
            override fun getTypeClass(): Effect = PlayShow()
            override fun getDisplayMaterial(): Material = Material.NETHER_STAR
        },
        SOUND_EFFECT{
            override fun getTypeClass(): Effect = SoundEffect()
            override fun getDisplayMaterial(): Material = Material.MUSIC_DISC_CAT
        },
        SOUND_EMITTER{
            override fun getTypeClass(): Effect = SoundEmitter()
            override fun getDisplayMaterial(): Material = Material.MUSIC_DISC_MALL
        };

        abstract fun getTypeClass(): Effect

        abstract fun getDisplayMaterial(): Material

        companion object {
            private val externalEffects = HashMap<String, me.m64diamondstar.effectmaster.utils.Pair<Effect, JavaPlugin>>()

            /**
             * Registers an external effect. This can be used for effects that are not part of the EffectMaster plugin.
             * @param effect The effect. Create a new instance of your effect class for this parameter.
             * @param plugin The plugin from where the effect is located.
             */
            fun registerExternalEffect(effect: Effect, plugin: JavaPlugin) {
                if(this.externalEffects.containsKey(effect.getIdentifier().uppercase())) {
                    plugin.logger.severe("Could not load external effect ${effect.getIdentifier()} from ${plugin.name}. " +
                                "An effect with the same identifier already seems to exist")
                    return
                }
                this.externalEffects[effect.getIdentifier().uppercase()] = me.m64diamondstar.effectmaster.utils.Pair(effect, plugin)
            }

            /**
             * Unregisters an external effect. This can be used for effects that are not part of the EffectMaster plugin.
             * @param identifier The identifier (or name) of the effect.
             */
            fun unregisterExternalEffect(identifier: String) {
                this.externalEffects.remove(identifier.uppercase())
            }

            /**
             * Returns an external effect. This can be used for effects that are not part of the EffectMaster plugin.
             * @param identifier The identifier (or name) of the effect.
             * @return The effect.
             */
            fun getExternalEffect(identifier: String): Effect? {
                return this.externalEffects[identifier.uppercase()]?.first
            }

            /**
             * Gets all external effects. This can be used for effects that are not part of the EffectMaster plugin.
             * @return A list of all the external effects. The list contains me.m64diamondstar.effectmaster.utils.Pairs of the effect and the source plugin.
             */
            fun getExternalEffects(): List<me.m64diamondstar.effectmaster.utils.Pair<Effect, JavaPlugin>> {
                return HashMap(externalEffects).values.toList()
            }

            /**
             * Checks if an effect exists (this is for both internal and external effects).
             * @param identifier The identifier (or name) of the effect.
             * @return True if the effect exists, false otherwise.
             */
            fun existsEffect(identifier: String): Boolean {
                return getExternalEffect(identifier) != null || values().any { it.name.uppercase() == identifier.uppercase() }
            }

            /**
             * Gets all internal effects. This can be used for effects that are not part of the EffectMaster plugin.
             * @return A map of all internal effects.
             */
            fun getInternalEffects(): List<Effect> {
                val list = ArrayList<Effect>()
                values().forEach { list.add(it.getTypeClass()) }
                return list
            }

            /**
             * Gets all effects. This can be used for effects that are not part of the EffectMaster plugin.
             * @return A map of all effects.
             */
            fun getAllEffects(): List<Effect> {
                val list = ArrayList<Effect>()
                values().forEach { list.add(it.getTypeClass()) }
                getExternalEffects().forEach { list.add(it.first) }
                return list
            }

            fun getEffect(identifier: String): Effect? {
                return getAllEffects().find { identifier.uppercase() == it.getIdentifier().uppercase() }
            }
        }
    }

    /**
     * @param players The list of players for whom the show needs to be visible. If this is null, the show will play for everyone.
     */
    abstract fun execute(players: List<Player>?, effectShow: EffectShow, id: Int)

    /**
     * @return the type of effect
     */
    abstract fun getIdentifier(): String

    /**
     * @return the display material
     */
    abstract fun getDisplayMaterial(): Material

    /**
     * Returns the description of the effect.
     */
    abstract fun getDescription(): String

    /**
     * @return true if the effect ran sync, false if it's ran async
     */
    abstract fun isSync(): Boolean

    /**
     * @return the default parameters of the effect
     */
    abstract fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>

    fun getSection(effectShow: EffectShow, id: Int): ConfigurationSection = effectShow.getConfig().getConfigurationSection("$id")!!
}