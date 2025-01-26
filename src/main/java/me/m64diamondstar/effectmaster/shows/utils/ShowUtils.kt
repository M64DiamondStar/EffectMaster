package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.Bukkit
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Item
import java.io.File
import java.util.UUID

object ShowUtils {

    private val fallingBlocks = HashSet<UUID>()
    private val droppedItems = HashSet<UUID>()

    fun getCategories(): ArrayList<File> {
        val file = File(EffectMaster.plugin().dataFolder, "shows")

        val files = ArrayList<File>()
        file.listFiles()?.let { files.addAll(it) }
        files.remove(File(EffectMaster.plugin().dataFolder, "shows/.DS_Store"))

        return files
    }

    fun getShows(category: String): ArrayList<File> {
        val file = File(EffectMaster.plugin().dataFolder, "shows/$category")

        val files = ArrayList<File>()
        file.listFiles()?.forEach {
            if(!it.name.contains(".DS_Store"))
                files.add(it)
        }

        return files
    }

    fun getCategory(string: String): File{
        return File(EffectMaster.plugin().dataFolder, "shows/$string")
    }

    private fun existsCategory(category: File): Boolean{
        File(EffectMaster.plugin().dataFolder, "shows").mkdirs()
        return File(EffectMaster.plugin().dataFolder, "shows").listFiles()!!.contains(category)
    }

    fun existsCategory(string: String): Boolean{
        return existsCategory(File(EffectMaster.plugin().dataFolder, "shows/$string"))
    }

    private fun existsShow(category: File, show: File): Boolean{
        return if(category.listFiles() == null)
            false
        else
            category.listFiles()!!.contains(show)
    }

    fun existsShow(category: String, show: String): Boolean{
        return existsShow(
            File(EffectMaster.plugin().dataFolder, "shows/$category"), File(
                EffectMaster.plugin().dataFolder,
                if(show.contains(".yml")) "shows/$category/$show" else "shows/$category/$show.yml")
        )
    }

    fun getFallingBlocks(): HashSet<FallingBlock> {
        return fallingBlocks.mapNotNull { Bukkit.getEntity(it) as FallingBlock? }.toHashSet()
    }

    fun addFallingBlock(fallingBlock: FallingBlock){
        fallingBlocks.add(fallingBlock.uniqueId)
    }

    fun removeFallingBlock(fallingBlock: FallingBlock){
        fallingBlocks.remove(fallingBlock.uniqueId)
    }

    fun containsFallingBlock(fallingBlockUUID: UUID): Boolean {
        return fallingBlocks.contains(fallingBlockUUID)
    }

    fun getDroppedItems(): HashSet<Item>{
        return return droppedItems.mapNotNull { Bukkit.getEntity(it) as Item? }.toHashSet()
    }

    fun addDroppedItem(item: Item){
        droppedItems.add(item.uniqueId)
    }

    fun removeDroppedItem(item: Item){
        droppedItems.remove(item.uniqueId)
    }

    fun containsDroppedItem(itemUUID: UUID): Boolean {
        return droppedItems.contains(itemUUID)
    }

}