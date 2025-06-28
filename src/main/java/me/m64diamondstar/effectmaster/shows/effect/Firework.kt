package me.m64diamondstar.effectmaster.shows.effect

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import me.m64diamondstar.effectmaster.shows.utils.DefaultDescriptions
import me.m64diamondstar.effectmaster.shows.utils.Parameter
import me.m64diamondstar.effectmaster.shows.utils.ShowSetting
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.IllegalArgumentException

class Firework() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int, settings: Set<ShowSetting>) {
        val location =
            if(settings.any { it.identifier == ShowSetting.Identifier.PLAY_AT }){
                LocationUtils.getRelativeLocationFromString(getSection(effectShow, id).getString("Location")!!,
                    effectShow.centerLocation ?: return)
                    ?.add(settings.find { it.identifier == ShowSetting.Identifier.PLAY_AT }!!.value as Location) ?: return
            }else
                LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
        val velocity = if (getSection(effectShow, id).get("Velocity") != null)
            if (LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!) != null)
                LocationUtils.getVectorFromString(getSection(effectShow, id).getString("Velocity")!!)!!
            else Vector(0.0, 0.0, 0.0)
        else Vector(0.0, 0.0, 0.0)

        val colors = if (getSection(effectShow, id).get("Colors") != null) Colors.getBukkitColorList(getSection(effectShow, id).getString("Colors")!!) else listOf(Color.WHITE)
        val fadeColors = if (getSection(effectShow, id).get("FadeColors") != null) Colors.getBukkitColorList(getSection(effectShow, id).getString("FadeColors")!!) else emptyList()

        val power = if (getSection(effectShow, id).get("Power") != null) getSection(effectShow, id).getInt("Power") else 1
        var shape = FireworkEffect.Type.BALL
        val shotAtAngle = if (getSection(effectShow, id).get("ShotAtAngle") != null) getSection(effectShow, id).getBoolean("ShotAtAngle") else false
        val flicker = if (getSection(effectShow, id).get("Flicker") != null) getSection(effectShow, id).getBoolean("Flicker") else false
        val trail = if (getSection(effectShow, id).get("Trail") != null) getSection(effectShow, id).getBoolean("Trail") else false

        try{
            shape = if(getSection(effectShow, id).get("FireworkShape") != null) FireworkEffect.Type.valueOf(getSection(effectShow, id).getString("FireworkShape")!!) else FireworkEffect.Type.BALL
        }catch (_: NullPointerException){ }

        val firework = location.world!!.spawnEntity(location, EntityType.FIREWORK_ROCKET) as Firework
        val fireworkMeta = firework.fireworkMeta

        try {
            firework.velocity = velocity
            firework.isShotAtAngle = shotAtAngle
            fireworkMeta.addEffect(
                FireworkEffect.builder()
                    .withColor(colors)
                    .withFade(fadeColors)
                    .flicker(flicker)
                    .trail(trail)
                    .with(shape)
                    .build()
            )

            if (power >= 0)
                fireworkMeta.power = power
            firework.fireworkMeta = fireworkMeta
        }catch (_: IllegalArgumentException){
            EffectMaster.plugin().logger.warning("Couldn't play Firework with ID $id from ${effectShow.getName()} in category ${effectShow.getCategory()}.")
            EffectMaster.plugin().logger.warning("The firework setting are not valid.")
        }

        if (players != null && EffectMaster.isProtocolLibLoaded)
            for (player in Bukkit.getOnlinePlayers()) {
                if (!players.contains(player)) {
                    val protocolManager = ProtocolLibrary.getProtocolManager()
                    val removePacket = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)
                    removePacket.intLists.write(0, listOf(firework.entityId))
                    protocolManager.sendServerPacket(player, removePacket)
                }
            }

        if(power < 0)
            firework.detonate()

    }

    override fun getIdentifier(): String {
        return "FIREWORK"
    }

    override fun getDisplayMaterial(): Material {
        return Material.FIREWORK_ROCKET
    }

    override fun getDescription(): String {
        return "Spawn a customizable firework rocket."
    }

    override fun isSync(): Boolean {
        return true
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
            "0, 0, 0",
            "Sets the velocity of the firework, used to launch a firework in a direction. If you have ShotAtAngle on false, it'll eventually automatically go upwards. Keep it at 0, 0, 0 if you want to launch it like a normal firework. Follows the pattern of x, y, z.",
            {it},
            { LocationUtils.getVectorFromString(it) != null })
        )
        list.add(Parameter(
            "Colors",
            "#ffffff, #000000",
            "The colors which the firework will use. Formatted as a list of hexadecimal colors following the pattern of color1, color2, color3 ect.",
            {it},
            { Colors.isColorList(it) })
        )
        list.add(Parameter(
            "FadeColors",
            "#ffffff, #000000",
            "The fade colors of the firework, follows the same pattern as the Colors parameter. Leave it empty to not use fade colors.",
            {it},
            { Colors.isColorList(it) })
        )
        list.add(Parameter(
            "Power",
            1,
            "The power of the firework from 0 - 127. Set it to -1 to let it explode instantaneously.",
            {it.toInt()},
            { it.toIntOrNull() != null && it.toInt() in -1..127 })
        )
        list.add(Parameter(
            "FireworkShape",
            "BALL",
            "The shape of the firework. Can be BALL, BALL_LARGE, BURST, CREEPER or STAR.",
            {it.uppercase()},
            { FireworkEffect.Type.entries.firstOrNull { firework -> firework.name == it } != null })
        )
        list.add(Parameter(
            "ShotAtAngle",
            false,
            "If the firework should be shot at an angle.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "Flicker",
            false,
            "If the firework should flicker.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
        )
        list.add(Parameter(
            "Trail",
            false,
            "If the firework should have a trail.",
            {it.toBoolean()},
            { it.toBooleanStrictOrNull() != null })
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