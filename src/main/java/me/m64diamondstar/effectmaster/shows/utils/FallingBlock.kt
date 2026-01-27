package me.m64diamondstar.effectmaster.shows.utils

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

object FallingBlockManager {
    private val fallingBlocks = ConcurrentHashMap.newKeySet<FallingBlock>()
    private var taskHandle: ScheduledTask? = null

    // Physics constants (matching vanilla Minecraft)
    private const val GRAVITY = 0.04
    private const val DRAG = 0.98
    private const val TERMINAL_VELOCITY = -3.92 // Prevents excessive falling speed

    fun addFallingBlock(fallingBlock: FallingBlock) {
        fallingBlocks.add(fallingBlock)
        ensureTaskRunning()
    }

    fun clear() {
        fallingBlocks.forEach { block ->
            if (!block.display.isDead) {
                block.display.remove()
            }
        }
        fallingBlocks.clear()
        stopTask()
    }

    private fun ensureTaskRunning() {
        if (taskHandle != null) return

        taskHandle = Bukkit.getServer().globalRegionScheduler.runAtFixedRate(
            EffectMaster.plugin(),
            { _ -> tick() },
            1,
            1
        )
    }

    private fun stopTask() {
        taskHandle?.let { handle ->
            handle.cancel()
            taskHandle = null
        }
    }

    private fun tick() {
        if (fallingBlocks.isEmpty()) {
            stopTask()
            return
        }

        val iterator = fallingBlocks.iterator()
        val toRemove = mutableListOf<FallingBlock>()

        while (iterator.hasNext()) {
            val block = iterator.next()

            // Check if block should be removed
            if (shouldRemoveBlock(block)) {
                toRemove.add(block)
                continue
            }

            // Update physics
            updateBlockPhysics(block)

            // Move block
            moveBlock(block)
        }

        // Clean up removed blocks
        toRemove.forEach { block ->
            fallingBlocks.remove(block)
            if (!block.display.isDead) {
                block.display.remove()
            }
        }
    }

    private fun shouldRemoveBlock(block: FallingBlock): Boolean {
        val display = block.display

        // Entity is dead
        if (display.isDead) return true

        // Block landed on solid ground
        val location = display.location
        val belowBlock = location.clone().subtract(0.0, 0.1, 0.0).block
        if (belowBlock.type.isSolid && block.velocity.y < 0) {
            return true
        }

        // Fallen too far (Y < -64 in most worlds)
        if (location.y < -128) {
            return true
        }

        return false
    }

    private fun updateBlockPhysics(block: FallingBlock) {
        // Apply gravity
        block.velocity.y = (block.velocity.y - GRAVITY).coerceAtLeast(TERMINAL_VELOCITY)

        // Apply drag
        block.velocity.multiply(DRAG)

        // Apply rotation if needed
        if (block.rotationSpeed != 0f) {
            block.currentRotation += block.rotationSpeed
            updateDisplayRotation(block)
        }
    }

    private fun updateDisplayRotation(block: FallingBlock) {
        val transform = block.display.transformation

        // Convert axis-angle to quaternion
        val axisAngle = AxisAngle4f(block.currentRotation, block.rotationAxis.x, block.rotationAxis.y, block.rotationAxis.z)
        val rotation = Quaternionf(axisAngle)

        block.display.transformation = Transformation(
            transform.translation,
            rotation,
            transform.scale,
            transform.rightRotation
        )
    }

    private fun moveBlock(block: FallingBlock) {
        val display = block.display
        val newLocation = display.location.add(block.velocity)

        // Use interpolation for smooth movement
        display.interpolationDuration = 1
        display.teleportDuration = 1
        display.teleport(newLocation)
    }
}

data class FallingBlock(
    val display: Display,
    var velocity: Vector,
    var rotationSpeed: Float = 0f,
    var rotationAxis: Vector3f = Vector3f(0f, 1f, 0f),
    var currentRotation: Float = 0f
)

/**
 * Spawn a falling block made from a Display Entity with optimized physics and rendering.
 *
 * @param blockData the block data to use for the display entity
 * @param location the location to spawn the falling block at
 * @param initialVelocity the velocity to apply to the entity
 * @param players the list of players who should see the entity, if null, it will be visible to all
 * @param rotationSpeed optional rotation speed for visual effect (in radians per tick)
 * @param rotationAxis optional axis for rotation (default: Y-axis)
 * @return the created FallingBlock instance
 */
fun emFallingBlock(
    blockData: BlockData,
    location: Location,
    initialVelocity: Vector,
    brightness: Int = -1,
    players: List<Player>? = null,
    rotationSpeed: Float = 0f,
    rotationAxis: Vector3f = Vector3f(0f, 1f, 0f)
): FallingBlock {
    val world = location.world
    val display: BlockDisplay = world.spawn(location.clone().add(0.0, 0.5, 0.0), BlockDisplay::class.java) { entity ->
        entity.block = blockData

        // Center the block visually
        entity.transformation = Transformation(
            Vector3f(-0.5f, -0.5f, -0.5f),
            Quaternionf(),
            Vector3f(1f),
            Quaternionf()
        )

        // Optimize for smooth interpolation
        entity.interpolationDuration = 1
        entity.teleportDuration = 2

        if (brightness in 0..15)
            entity.brightness = Display.Brightness(brightness, brightness)
        entity.viewRange = 1.0f // Adjust based on your needs (lower = better performance)
        entity.isPersistent = false

        // Player visibility
        if (players != null) {
            entity.isVisibleByDefault = false
            players.forEach { player ->
                player.showEntity(EffectMaster.plugin(), entity)
            }
        }
    }

    val fallingBlock = FallingBlock(
        display = display,
        velocity = initialVelocity.clone(),
        rotationSpeed = rotationSpeed,
        rotationAxis = rotationAxis
    )

    FallingBlockManager.addFallingBlock(fallingBlock)

    return fallingBlock
}

/**
 * Create a falling block with random rotation for more dynamic visuals
 */
fun emFallingBlockWithRotation(
    blockData: BlockData,
    location: Location,
    initialVelocity: Vector,
    brightness: Int = -1,
    players: List<Player>? = null,
    rotationSpeedMultiplier: Float = 1f
): FallingBlock {
    val randomRotationSpeed = (Random.nextFloat() * 0.2f - 0.1f) * rotationSpeedMultiplier // Random rotation between -0.1 and 0.1 rad/tick
    val randomAxis = Vector3f(
        Random.nextFloat(),
        Random.nextFloat(),
        Random.nextFloat()
    ).normalize()

    return emFallingBlock(blockData, location, initialVelocity, brightness, players, randomRotationSpeed, randomAxis)
}

/**
 * Spawn a falling block made from a Display Entity with optimized physics and rendering.
 *
 * @param blockData the block data to use for the display entity
 * @param location the location to spawn the falling block at
 * @param initialVelocity the velocity to apply to the entity
 * @param players the list of players who should see the entity, if null, it will be visible to all
 * @return the created FallingBlock instance
 */
fun emFallingBlock(
    blockData: BlockData,
    location: Location,
    initialVelocity: Vector,
    brightness: Int = -1,
    rotate: Boolean = false,
    rotationSpeedMultiplier: Float = 1f,
    players: List<Player>? = null
): FallingBlock {
    return if(rotate)
        emFallingBlockWithRotation(blockData, location, initialVelocity, brightness, players, rotationSpeedMultiplier)
    else
        emFallingBlock(blockData, location, initialVelocity, brightness, players)
}