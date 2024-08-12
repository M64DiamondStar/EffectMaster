package me.m64diamondstar.effectmaster.shows.type

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import me.m64diamondstar.effectmaster.EffectMaster
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.locations.LocationUtils
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.lang.IllegalArgumentException

class Firework() : Effect() {

    override fun execute(players: List<Player>?, effectShow: EffectShow, id: Int) {
        val location = LocationUtils.getLocationFromString(getSection(effectShow, id).getString("Location")!!) ?: return
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

        val firework = location.world!!.spawnEntity(location, EntityType.FIREWORK) as Firework
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

    override fun getDefaults(): List<me.m64diamondstar.effectmaster.utils.Pair<String, Any>> {
        val list = ArrayList<me.m64diamondstar.effectmaster.utils.Pair<String, Any>>()
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Type", "FIREWORK"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Location", "world, 0, 0, 0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Velocity", "0.0, 0.0, 0.0"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Colors", "#ffffff, #000000"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("FadeColors", " "))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Power", 1))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("FireworkShape", "BALL"))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("ShotAtAngle", false))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Flicker", false))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Trail", false))
        list.add(me.m64diamondstar.effectmaster.utils.Pair("Delay", 0))
        return list
    }
}