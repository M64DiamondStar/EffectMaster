package me.m64diamondstar.effectmaster.editor.wand.path

import me.m64diamondstar.effectmaster.editor.wand.Wand
import me.m64diamondstar.effectmaster.editor.wand.WandMode
import me.m64diamondstar.effectmaster.editor.wand.WandMode.Action
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.locations.Spline
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import me.m64diamondstar.effectmaster.utils.clickedBlockLocation
import me.m64diamondstar.effectmaster.utils.isLeftClick
import me.m64diamondstar.effectmaster.utils.isRightClick
import me.m64diamondstar.effectmaster.utils.nextEnumValue
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import kotlin.math.pow

class PathWand: Wand("path", Colors.format("#f58a42&lPath Wand")) {

    private val modes = listOf(
        AddNode(this),
        DeleteNode(this),
        MoveNode(this),
        Info(this)
    )

    private val sessions = HashMap<UUID, PathWandSession>()

    override fun getPermission(): String = "effectmaster.wands.path"

    override fun getModes(): List<WandMode> {
        return modes
    }

    fun getSession(player: Player): PathWandSession{
        return sessions.get(player.uniqueId) ?: createSession(player)!!
    }

    fun createSession(player: Player): PathWandSession? {
        if(sessions.containsKey(player.uniqueId)) return null
        val session = PathWandSession(player)
        sessions[player.uniqueId] = session
        return session
    }

    data class Node(var location: Location)

    class PathWandSession(val player: Player){

        private val nodes = ArrayList<Node>()

        var displayProgress = 0.0
        var displayType = Spline.POLY_CHAIN
        var displaySpeed = 10
        var selectedNodes = mutableListOf<Node>()
        var lastSelect = 0L
        fun getAllNodes(): List<Node> = nodes
        fun addNode(location: Location) = Node(location).also { nodes.add(it) }
        fun deleteNode(node: Node?) = nodes.remove(node)
        fun moveNode(location: Location) {
            if(selectedNodes.size > 1) return
            selectedNodes.first().location = location
        }

        fun checkClick(): Node?{
            val closestNode = nodes
                .sortedBy { player.location.distanceSquared(it.location) }
                .firstOrNull { node ->
                    player.location.distanceSquared(node.location) <= 10.0.pow(2) &&
                            isPlayerLookingAt(player, node.location)
            }
            return closestNode
        }

        fun displayParticles(){
            val normalNodes = ArrayList<Node>()
            normalNodes.addAll(nodes)
            normalNodes.removeAll(selectedNodes)

            normalNodes.forEach {
                val dustOptions = Particle.DustOptions(Color.fromRGB(255, 255, 255), 1F)
                player.spawnParticle(Particle.DUST, it.location, 1, 0.0, 0.0, 0.0, 5.0, dustOptions, true)
            }

            selectedNodes.forEach {
                val dustOptions = Particle.DustOptions(Color.fromRGB(210, 3, 252), 1F)
                player.spawnParticle(Particle.DUST, it.location, 1, 0.0, 0.0, 0.0, 5.0, dustOptions, true)
            }
        }

        private fun isPlayerLookingAt(player: Player, target: Location): Boolean {
            val range = 20
            val boxSize = 0.8
            val eyeLocation = player.eyeLocation
            val direction = eyeLocation.direction.normalize()
            val origin = eyeLocation.toVector()

            // Create AABB (axis-aligned bounding box)
            val half = boxSize / 2.0
            val min = target.clone().subtract(half, half, half).toVector()
            val max = target.clone().add(half, half, half).toVector()

            // Ray trace in small steps
            val step = 0.1
            var distance = 0.0
            while (distance <= range) {
                val point = origin.clone().add(direction.clone().multiply(distance))
                if (point.isInAABB(min, max)) return true
                distance += step
            }

            return false
        }

        fun select(){
            val isDouble = System.currentTimeMillis() - lastSelect < 1000
            val node = checkClick()
            if(node == null){
                selectedNodes.clear()
                return
            }

            // Multiple selecting
            if (player.isSneaking) {
                if (isDouble) {
                    val allNodes = getAllNodes()
                    val previouslySelected = selectedNodes.minus(node).toList()

                    if (previouslySelected.size == 1) {
                        val from = allNodes.indexOf(previouslySelected.first())
                        val to = allNodes.indexOf(node)

                        if (from != -1 && to != -1) {
                            selectedNodes.clear()
                            val range = if (from <= to) from..to else to..from
                            selectedNodes.addAll(allNodes.slice(range))
                        } else {
                            // Fallback: just select the node
                            selectedNodes.clear()
                            selectedNodes.add(node)
                        }
                    } else {
                        selectedNodes.clear()
                        selectedNodes.add(node)
                    }
                } else {
                    if (!selectedNodes.remove(node))
                        selectedNodes.add(node)
                }
            // Single selecting
            } else {
                selectedNodes.clear()
                if (isDouble) {
                    selectedNodes.addAll(getAllNodes())
                } else {
                    selectedNodes.add(node)
                }
            }

            // Update last selected for multiple selection
            lastSelect = System.currentTimeMillis()
        }

    }

    /**
     * Class used to add a new node
     */
    private class AddNode(private val wand: PathWand): WandMode {
        override fun onInteract(event: PlayerInteractEvent) {
            event.isCancelled = true

            val player = event.player
            val session = wand.getSession(player)

            if (event.isRightClick()) {
                session.addNode(event.clickedBlockLocation() ?: player.location)
            }

            if (event.isLeftClick())
                session.select()
        }


        override fun getId(): String {
            return "add_mode"
        }

        override fun getMaterial(): Material {
            return Material.BAMBOO
        }

        override fun getDisplay(): String {
            return "Add Node"
        }

        override fun getDescription(): List<Action> {
            return listOf(
                Action(WandMode.ModeAction.RIGHT_CLICK, "Create a new node at your feet."),
                Action(WandMode.ModeAction.LEFT_CLICK, "Select the node you're looking at."),
                Action(WandMode.ModeAction.LEFT_CLICK_SHIFT, "Select multiple nodes.")
            )
        }

        override fun getPermission(): String? {
            return null
        }

        override fun task(player: Player) {
            wand.getSession(player).displayParticles()
        }
    }

    /**
     * Class used to delete a node
     */
    private class DeleteNode(private val wand: PathWand): WandMode {
        override fun onInteract(event: PlayerInteractEvent) {
            event.isCancelled = true

            val player = event.player
            val session = wand.getSession(player)
            if(event.isRightClick()){
                session.selectedNodes.forEach { session.deleteNode(it) }
                session.selectedNodes.clear()
            }

            if (event.isLeftClick())
                session.select()
        }

        override fun getId(): String {
            return "delete_mode"
        }

        override fun getMaterial(): Material {
            return Material.CRIMSON_FUNGUS
        }

        override fun getDisplay(): String {
            return "Delete Node"
        }

        override fun getDescription(): List<Action> {
            return listOf(
                Action(WandMode.ModeAction.RIGHT_CLICK, "Delete all selected nodes."),
                Action(WandMode.ModeAction.LEFT_CLICK, "Select the node you're looking at."),
                Action(WandMode.ModeAction.LEFT_CLICK_SHIFT, "Select multiple nodes.")
            )
        }

        override fun getPermission(): String? {
            return null
        }

        override fun task(player: Player) {
            wand.getSession(player).displayParticles()
        }
    }

    /**
     * Mode used to move a location node
     */
    private class MoveNode(private val wand: PathWand): WandMode {
        override fun onInteract(event: PlayerInteractEvent) {
            event.isCancelled = true

            val player = event.player
            val session = wand.getSession(player)
            if (event.isRightClick())
                session.moveNode(
                    event.clickedBlockLocation() ?: player.eyeLocation.clone()
                        .add(player.eyeLocation.direction.normalize().multiply(2))
                )

            if (event.isLeftClick())
                session.select()
        }

        override fun getId(): String {
            return "move_mode"
        }

        override fun getMaterial(): Material {
            return Material.ALLIUM
        }

        override fun getDisplay(): String {
            return "Move Node"
        }

        override fun getDescription(): List<Action> {
            return listOf(
                Action(WandMode.ModeAction.RIGHT_CLICK, "Move the selected node in front of you."),
                Action(WandMode.ModeAction.LEFT_CLICK, "Select the node you're looking at."),
                Action(WandMode.ModeAction.LEFT_CLICK_SHIFT, "Select multiple nodes.")
            )
        }

        override fun getPermission(): String? {
            return null
        }

        override fun task(player: Player) {
            wand.getSession(player).displayParticles()
        }

    }

    /**
     * Mode used to retrieve all the necessary info of the path. It also displays the path in different ways
     */
    private class Info(private val wand: PathWand): WandMode {
        private val speedSettings = intArrayOf(1, 3, 5, 7, 10, 15, 20, 30)

        override fun onInteract(event: PlayerInteractEvent) {
            event.isCancelled = true

            val player = event.player
            val session = wand.getSession(player)
            if (event.isRightClick()){
                if(player.isSneaking){
                    val newIndex = speedSettings.indexOf(session.displaySpeed) + 1
                    session.displaySpeed = if(newIndex >= speedSettings.size) 1 else speedSettings[newIndex]
                    player.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toString() + "Changed display speed to ${session.displaySpeed}"))
                }else {
                    session.displayType = nextEnumValue(session.displayType)
                    player.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toString() + "Changed display spline to ${session.displayType}"))
                    checkDisplayType(player)
                }
            }

            if (event.isLeftClick()) {
                player.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "---------------------"))

                (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize("<${Colors.Color.DEFAULT}>" +
                        "<b>       [Click to copy path]")
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                        LocationUtils.getStringFromPath(session.getAllNodes().map { it.location })))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                        MiniMessage.miniMessage().deserialize("<${Colors.Color.DEFAULT}>Click to copy the path to your clipboard.")))
                )

                (player as Audience).sendMessage(MiniMessage.miniMessage().deserialize("<${Colors.Color.DEFAULT}>" +
                        "<b>       [Click to send edit]")
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/em enter ${LocationUtils.getStringFromPath(session.getAllNodes().map { it.location })}"))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT,
                        MiniMessage.miniMessage().deserialize("<${Colors.Color.DEFAULT}>Click to use this path in the /em edit command.")))
                )

                player.sendMessage(Colors.format(Prefix.PrefixType.DEFAULT.toShortString() + "---------------------"))
            }
        }

        override fun onEquip(player: Player) {
            checkDisplayType(player)
        }

        fun checkDisplayType(player: Player){
            val session = wand.getSession(player)
            if(session.getAllNodes().size < session.displayType.minPoints()){
                player.sendMessage(Colors.format(Colors.Color.ERROR.toString() +
                        "Spline type ${session.displayType} needs at least " +
                        "${session.displayType.minPoints()} nodes."))
            }
        }

        override fun getId(): String {
            return "info"
        }

        override fun getMaterial(): Material {
            return Material.BOOK
        }

        override fun getDisplay(): String {
            return "Overview"
        }

        override fun getDescription(): List<Action> {
            return listOf(
                Action(WandMode.ModeAction.LEFT_CLICK, "Receive a copy button in the chat."),
                Action(WandMode.ModeAction.RIGHT_CLICK, "Switch path mode (poly, bezier's or catmull-rom)."),
                Action(WandMode.ModeAction.RIGHT_CLICK_SHIFT, "Change the speed of the animation.")
            )
        }

        override fun getPermission(): String? {
            return null
        }

        override fun task(player: Player) {
            val session = wand.getSession(player)

            if (session.displayProgress >= 1) // Reset display progress when it has reached the end
                session.displayProgress = 0.0

            val path = session.getAllNodes().map { it.location }
            val speed = session.displaySpeed * 0.05
            val splineType = session.displayType
            var distance = 0.0
            for (loc in 1 until path.size) {
                distance += path[loc - 1].distance(path[loc])
            }
            val duration = distance / speed

            try {
                if (duration / distance < 5) {
                    val entitiesPerTick = 5 / (duration / distance)
                    for (i2 in 1..entitiesPerTick.toInt()){
                        val c = session.displayProgress + 1.0 / duration / entitiesPerTick * i2
                        if(c > 1){
                            session.displayProgress = 0.0
                            return
                        }
                        player.spawnParticle(Particle.END_ROD, splineType.calculate(path, c), 1, 0.0, 0.0, 0.0, 0.0, null, true)
                    }
                }
                player.spawnParticle(Particle.END_ROD, splineType.calculate(path, session.displayProgress), 1, 0.0, 0.0, 0.0, 0.0, null, true)
            }catch (_: Exception){} // Silence the errors it creates when the spline is not supported on a low amount of nodes

            session.displayParticles()
            session.displayProgress += 1.0 / duration
        }
    }
}