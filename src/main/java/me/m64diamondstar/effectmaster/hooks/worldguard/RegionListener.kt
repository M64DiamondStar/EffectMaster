package me.m64diamondstar.effectmaster.hooks.worldguard

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class RegionListener : Listener {

    private val regionPlayers = mutableMapOf<String, MutableSet<UUID>>() // regionId -> UUIDs
    private val delayTasks = mutableMapOf<Pair<String, UUID>, ScheduledTask>() // (regionId, playerId) -> task

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.isCancelled) return
        val flags = EffectMaster.getWorldGuardManager() ?: return

        val to = event.to ?: return
        val from = event.from
        if (from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return

        val player = event.player
        val playerId = player.uniqueId

        val container = WorldGuard.getInstance().platform.regionContainer
        val world = BukkitAdapter.adapt(to.world)
        val regions = container[world] ?: return

        val fromRegions = regions.getApplicableRegions(BlockVector3.at(from.blockX, from.blockY, from.blockZ))
        val toRegions = regions.getApplicableRegions(BlockVector3.at(to.blockX, to.blockY, to.blockZ))

        val entered = toRegions.regions - fromRegions.regions
        val exited = fromRegions.regions - toRegions.regions

        for (region in exited) {
            val id = region.id
            val players = regionPlayers.getOrPut(id) { mutableSetOf() }
            players.remove(playerId)
            val lastExit = players.isEmpty()

            // Cancel any delayed enter tasks (player left before delay finished)
            cancelDelayedTask(id, playerId)

            // Instant exit
            region.getFlag(flags.exitFlag)?.let { playShowNow(it) }

            // Last exit
            if (lastExit) {
                region.getFlag(flags.lastExitFlag)?.let { playShowNow(it) }
            }

            // Delayed exit
            region.getFlag(flags.delayExitFlag)?.let { (category, name, delay) ->
                startDelayedShow(id, player, category, name, delay)
            }

            // Delayed last exit
            if (lastExit) {
                region.getFlag(flags.delayLastExitFlag)?.let { (category, name, delay) ->
                    startDelayedShow(id, player, category, name, delay)
                }
            }
        }

        for (region in entered) {
            val id = region.id
            val players = regionPlayers.getOrPut(id) { mutableSetOf() }
            val firstEnter = players.isEmpty()
            players.add(playerId)

            // Cancel any delayed exit tasks (player re-entered before delay finished)
            cancelDelayedTask(id, playerId)

            // Instant enter
            region.getFlag(flags.enterFlag)?.let { playShowNow(it) }

            // First enter
            if (firstEnter) {
                region.getFlag(flags.firstEnterFlag)?.let { playShowNow(it) }
            }

            // Delayed enter
            region.getFlag(flags.delayEnterFlag)?.let { (category, name, delay) ->
                startDelayedShow(id, player, category, name, delay)
            }

            // Delayed first enter
            if (firstEnter) {
                region.getFlag(flags.delayFirstEnterFlag)?.let { (category, name, delay) ->
                    startDelayedShow(id, player, category, name, delay)
                }
            }
        }
    }

    private fun playShowNow(flagValue: Pair<String, String>) {
        val (category, name) = flagValue
        if (ShowUtils.existsShow(category, name)) {
            EffectShow(category, name).play(null)
        }
    }

    private fun startDelayedShow(regionId: String, player: Player, category: String, name: String, delay: Long) {
        val key = Pair(regionId, player.uniqueId)

        // Cancel any existing delayed task for this region and player
        // This prevents multiple tasks from stacking up if the player moves in and out of the region quickly
        val currentTask = delayTasks[key]
        currentTask?.cancel()

        val task = Bukkit.getGlobalRegionScheduler().runDelayed(EffectMaster.plugin(), {
            if (ShowUtils.existsShow(category, name)) {
                EffectShow(category, name).play(null)
            }
            delayTasks.remove(key)
        }, delay)

        delayTasks[key] = task
    }

    private fun cancelDelayedTask(regionId: String, playerId: UUID) {
        val key = Pair(regionId, playerId)
        val task = delayTasks[key] ?: return
        task.cancel()
    }
}
