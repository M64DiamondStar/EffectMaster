package me.m64diamondstar.effectmaster.shows.effect

import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.collections.forEach
import kotlin.math.roundToInt
import kotlin.random.Random

class SnowLauncher: Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        try{
            val location =
                if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                    LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                        effectShow.centerLocation ?: return)
                        ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
                }else
                    LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
            val amount = if (getSection(effectShow, id).get("Amount") != null) getSection(effectShow, id).getInt("Amount") else 3
            val spread = if (getSection(effectShow, id).get("Spread") != null) getSection(effectShow, id).getDouble("Spread") else 0.3
            val velocity =
                if (getSection(effectShow, id).get("Velocity") != null)
                    if (LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!) != null)
                        LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!)!!
                    else Vector(0.0, 0.0, 0.0)
                else Vector(0.0, 0.0, 0.0)
            val duration = if (getSection(effectShow, id).get("Duration") != null) getSection(effectShow, id).getInt("Duration") else {
                if (getSection(effectShow, id).get("Length") != null) getSection(effectShow, id).getInt("Length") else 20
            }
            val interval = if (getSection(effectShow, id).get("Interval") != null) getSection(effectShow, id).getInt("Interval") else 3
            val force = if (getSection(effectShow, id).get("Force") != null) getSection(effectShow, id).getBoolean("Force") else false
            val startUp = if (getSection(effectShow, id).get("StartUp") != null) getSection(effectShow, id).getDouble("StartUp") else 0.0

            val playAmount = duration / interval

            var c = 0
            EffectMaster.getFoliaLib().scheduler.runTimer({ task ->
                if (c >= playAmount) {
                    task.cancel()
                    return@runTimer
                }
                repeat(if (startUp > 0.0 && c <= startUp) (c.toDouble() / startUp * amount.toDouble()).roundToInt() else amount) {
                    var newVelocity = velocity

                    if (spread != 0.0)
                        newVelocity = Vector(
                            velocity.x + (Random.nextInt(0, 1000).toDouble() / 1000 * spread * 2 - spread),
                            velocity.y + (Random.nextInt(0, 1000).toDouble() / 1000 * spread * 2 - spread / 3),
                            velocity.z + (Random.nextInt(0, 1000).toDouble() / 1000 * spread * 2 - spread)
                        )

                    if (players == null) {
                        location.world!!.spawnParticle(Particle.SNOWFLAKE, location, 0, newVelocity.x, newVelocity.y, newVelocity.z, 1.0, null, force)
                    } else {
                        players.forEach {
                            it.spawnParticle(Particle.SNOWFLAKE, location, 0, newVelocity.x, newVelocity.y, newVelocity.z, 1.0, null)
                        }
                    }
                }
                c++
            }, 0L, interval.toLong())


        }catch (_: Exception){
            EffectMaster.plugin().logger.warning("Couldn't play Particle Emitter with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("Possible errors: ")
            EffectMaster.plugin().logger.warning("- The particle you entered doesn't exist.")
            EffectMaster.plugin().logger.warning("- The location/world doesn't exist or is unloaded")
        }
    }

    override fun getIdentifier(): String {
        return "SNOW_LAUNCHER"
    }

    override fun getDisplayMaterial(): Material {
        return Material.SNOWBALL
    }

    override fun getDescription(): String {
        return "Emits snow particles with spread over a specific time span."
    }

    override fun isSync(): Boolean {
        return false
    }

    override fun getDefaults(): List<Parameter> {
        val list = ArrayList<Parameter>()
        list.add(Parameter(
            "Location",
            "world, 0, 0, 0",
            DefaultDescriptions.LOCATION,
            {it},
            { LocationUtils.getLocationFromString(it) != null })
        )
        list.add(Parameter(
            "Velocity",
            "0, 1, 0",
            DefaultDescriptions.VELOCITY,
            {it},
            { LocationUtils.getVectorFromString(it) != null })
        )
        list.add(Parameter(
            "Amount",
            3,
            "The amount of particles to spawn.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() >= 0 })
        )
        list.add(Parameter(
            "Spread",
            0.3,
            "Works about the same as the randomizer parameter, it spreads out the shots a little bit.",
            {it.toDouble()},
            { it.toDoubleOrNull() != null && it.toDouble() >= 0 })
        )
        list.add(Parameter(
            "Force",
            false,
            "Whether the particle should be forcibly rendered by the player or not.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "Duration",
            20,
            DefaultDescriptions.DURATION,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        list.add(Parameter(
            "Interval",
            1,
            "Every how many ticks should a particle be shot?",
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        list.add(Parameter(
            "StartUp",
            0,
            "The time it takes to display the full amount of particles. Set to 0 to disable it.",
            {it.toInt()},
            { it.toFloatOrNull() != null && it.toFloat() >= 0.0 })
        )
        list.add(Parameter(
            "Delay",
            0,
            DefaultDescriptions.DELAY,
            {it.toInt()},
            { it.toLongOrNull() != null && it.toLong() >= 0 })
        )
        return list
    }
}