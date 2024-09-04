package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.effect.*
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class Effect() {

    enum class Type {
        ANIMATRONIC {
            override fun getTypeClass(): Effect = Animatronic()
        },
        ANIMATRONIC_GROUP {
            override fun getTypeClass(): Effect = AnimatronicGroup()
        },
        FALLING_BLOCK {
            override fun getTypeClass(): Effect = FallingBlock()
        },
        SET_BLOCK {
            override fun getTypeClass(): Effect = SetBlock()
        },
        FILL_BLOCK {
            override fun getTypeClass(): Effect = FillBlock()
        },
        REPLACE_FILL {
            override fun getTypeClass(): Effect = ReplaceFill()
        },
        BLOCK_LINE {
            override fun getTypeClass(): Effect = BlockLine()
        },
        BLOCK_PATH {
            override fun getTypeClass(): Effect = BlockPath()
        },
        FOUNTAIN {
            override fun getTypeClass(): Effect = Fountain()
        },
        FOUNTAIN_BLOOM {
            override fun getTypeClass(): Effect = FountainBloom()
        },
        FOUNTAIN_DANCING {
            override fun getTypeClass(): Effect = FountainDancing()
        },
        FOUNTAIN_LINE{
            override fun getTypeClass(): Effect = FountainLine()
        },
        FOUNTAIN_PATH {
            override fun getTypeClass(): Effect = FountainPath()
        },
        ITEM_FOUNTAIN{
            override fun getTypeClass(): Effect = ItemFountain()
        },
        ITEM_FOUNTAIN_LINE{
            override fun getTypeClass(): Effect = ItemFountainLine()
        },
        ITEM_FOUNTAIN_PATH {
            override fun getTypeClass(): Effect = ItemFountainPath()
        },
        PARTICLE {
            override fun getTypeClass(): Effect = Particle()
        },
        PARTICLE_EMITTER {
            override fun getTypeClass(): Effect = ParticleEmitter()
        },
        PARTICLE_LINE {
            override fun getTypeClass(): Effect = ParticleLine()
        },
        PARTICLE_PATH {
            override fun getTypeClass(): Effect = ParticlePath()
        },
        ACTIVATOR{
            override fun getTypeClass(): Effect = Activator()
        },
        CONSOLE_COMMAND{
            override fun getTypeClass(): Effect = ConsoleCommand()
        },
        FIREWORK{
            override fun getTypeClass(): Effect = Firework()
        },
        PLAY_SHOW{
            override fun getTypeClass(): Effect = PlayShow()
        },
        SOUND_EFFECT{
            override fun getTypeClass(): Effect = SoundEffect()
        },
        SOUND_EMITTER{
            override fun getTypeClass(): Effect = SoundEmitter()
        };

        abstract fun getTypeClass(): Effect

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
                return getExternalEffect(identifier) != null || Type.entries.any { it.name.uppercase() == identifier.uppercase() }
            }

            /**
             * Gets all internal effects. This can be used for effects that are not part of the EffectMaster plugin.
             * @return A map of all internal effects.
             */
            fun getInternalEffects(): List<Effect> {
                val list = ArrayList<Effect>()
                Type.entries.forEach { list.add(it.getTypeClass()) }
                return list
            }

            /**
             * Gets all effects. This can be used for effects that are not part of the EffectMaster plugin.
             * @return A map of all effects.
             */
            fun getAllEffects(): List<Effect> {
                val list = ArrayList<Effect>()
                Type.entries.forEach { list.add(it.getTypeClass()) }
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
    abstract fun getDefaults(): List<Parameter>

    fun getSection(effectShow: EffectShow, id: Int): ConfigurationSection = effectShow.getConfig().getConfigurationSection("$id")!!
}