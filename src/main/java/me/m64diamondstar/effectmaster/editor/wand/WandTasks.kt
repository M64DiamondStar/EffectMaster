package me.m64diamondstar.effectmaster.editor.wand

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.editor.wand.event.WandUnequipEvent
import me.m64diamondstar.effectmaster.utils.Colors
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object WandTasks {

    private val holders = mutableMapOf<UUID, Pair<Wand, WandMode>>()

    fun addHolder(player: Player, wand: Wand, wandMode: WandMode){
        holders[player.uniqueId] = Pair(wand, wandMode)
    }

    fun removeHolder(player: Player){
        holders.remove(player.uniqueId)
    }

    fun removeHolder(uniqueId: UUID){
        holders.remove(uniqueId)
    }

    /**
     * This method should only be called in EffectMaster's onEnable
     */
    fun initialize() {
        EffectMaster.getFoliaLib().scheduler.runTimer({ task ->

            holders.forEach { uuid, (wand, wandMode) ->
                val player = Bukkit.getPlayer(uuid) ?: return@forEach.also { removeHolder(uuid) }
                if(!Wand.isWand(player.inventory.itemInMainHand)) return@forEach.also {
                    removeHolder(uuid)
                    val unequipEvent = WandUnequipEvent(player, wand)
                    Bukkit.getPluginManager().callEvent(unequipEvent)
                }

                (player as Audience).sendActionBar(MiniMessage.miniMessage().deserialize(
                    "<${Colors.Color.DEFAULT}>Mode: ${wandMode.getDisplay()}"
                ))

                wandMode.task(player)
            }

        }, 0L, 1L)
    }

}