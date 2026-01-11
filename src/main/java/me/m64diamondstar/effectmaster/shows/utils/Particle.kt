package me.m64diamondstar.effectmaster.shows.utils

import com.destroystokyo.paper.ParticleBuilder
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.update.Version
import org.bukkit.*
import org.bukkit.block.BlockType
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Creates a ParticleBuilder object based on the parameters
 * @param color the color for color-able particles (FLASH, TINTED_LEAVES, DUST), also the first color for DUST_COLOR_TRANSITION
 * @param alpha the transparency for the ENTITY_EFFECT particle
 * @param size the size for DUST and DUST_COLOR_TRANSITION particles
 * @param toColor the 2nd color for DUST_COLOR_TRANSITION
 * @param trail the Trail object for TRAIL
 * @param blockData the BlockData object for BLOCK, BLOCK_CRUMBLE and BLOCK_MARKER, DUST_PILLAR and FALLING_DUST
 * @param itemStack the ItemStack for ITEM
 * @param angle the angle for the SKULK_CHARGE particle
 * @param vibration the Vibration object for VIBRATION
 */
fun emParticle(
    type: Particle,
    location: Location,
    offset: Triple<Double, Double, Double>,
    count: Int = 1,
    speed: Double = 1.0,
    color: Color = Color.BLACK, // Color-able particles
    alpha: Int = 255, // ENTITY_EFFECT particle
    size: Float = 2f, // DUST, DUST_COLOR_TRANSITION
    toColor: Color = Color.WHITE, // DUST_COLOR_TRANSITION particle
    trail: Particle.Trail = Particle.Trail(location, Color.BLACK, 40), // TRAIL particle
    blockData: BlockData = BlockType.STONE.createBlockData(), // BLOCK, BLOCK_CRUMBLE, BLOCK_MARKER particle
    itemStack: ItemStack = ItemStack.of(Material.STONE), // ITEM particle
    angle: Double = 0.0, // SKULK_CHARGE particle
    vibration: Vibration = Vibration(Vibration.Destination.BlockDestination(location), 40), // VIBRATION particle
    receivers: List<Player>? = null, // List of players who should see the particle
    receiveRadius: Int = 32
): ParticleBuilder {
    // Create a new builder
    val builder = ParticleBuilder(type)
        .location(location)
        .offset(offset.first, offset.second, offset.third)
        .count(count)
        .extra(speed)

    // Select correct viewers
    if(receivers != null)
        builder.receivers(receivers)
    else
        builder.receivers(receiveRadius, true)

    // Check different cases and returns the particle
    return when(type.name) { // Check name instead of enum to avoid exceptions on older minecraft versions
        "ENTITY_EFFECT" -> {
            builder
                .data(Color.fromARGB(alpha, color.red, color.green, color.blue))
        }

        "FLASH" -> {
            if(EffectMaster.getVersion() >= Version.parse("1.21.9"))
                builder.data(color)
            else
                builder
        }

        "TINTED_LEAVES" -> {
            builder.data(color)
        }

        "DUST" -> {
            builder.color(color, size)
        }

        "DUST_COLOR_TRANSITION" -> {
            builder.colorTransition(color, toColor, size)
        }

        "TRAIL" -> {
            builder.data(trail)
        }

        "BLOCK", "BLOCK_CRUMBLE", "BLOCK_MARKER", "DUST_PILLAR", "FALLING_DUST" -> {
            builder.data(blockData)
        }

        "ITEM" -> {
            builder.data(itemStack)
        }

        "SCULK_CHARGE" -> {
            builder.data(Math.toRadians(angle).toFloat()).extra(speed)
        }

        "VIBRATION" -> {
            builder.data(vibration)
        }

        else -> {
            builder
        }
    }
}