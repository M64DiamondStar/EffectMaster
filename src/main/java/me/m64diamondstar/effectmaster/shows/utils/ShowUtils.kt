package me.m64diamondstar.effectmaster.shows.utils

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Item
import java.io.File

object ShowUtils {

    private val fallingBlocks = ArrayList<FallingBlock>()
    private val droppedItems = ArrayList<Item>()

    fun getCategories(): ArrayList<File> {
        val file = File(EffectMaster.plugin.dataFolder, "shows")

        val files = ArrayList<File>()
        file.listFiles()?.let { files.addAll(it) }
        files.remove(File(EffectMaster.plugin.dataFolder, "shows/.DS_Store"))

        return files
    }

    fun getShows(category: String): ArrayList<File> {
        val file = File(EffectMaster.plugin.dataFolder, "shows/$category")

        val files = ArrayList<File>()
        file.listFiles()?.forEach {
            if(!it.name.contains(".DS_Store"))
                files.add(it)
        }

        return files
    }

    private fun existsCategory(category: File): Boolean{
        File(EffectMaster.plugin.dataFolder, "shows").mkdirs()
        return File(EffectMaster.plugin.dataFolder, "shows").listFiles()!!.contains(category)
    }

    fun existsCategory(string: String): Boolean{
        return existsCategory(File(EffectMaster.plugin.dataFolder, "shows/$string"))
    }

    private fun existsShow(category: File, show: File): Boolean{
        return category.listFiles()!!.contains(show)
    }

    fun existsShow(category: String, show: String): Boolean{
        return existsShow(
            File(EffectMaster.plugin.dataFolder, "shows/$category"), File(
                EffectMaster.plugin.dataFolder,
                if(show.contains(".yml")) "shows/$category/$show" else "shows/$category/$show.yml")
        )
    }

    fun getFallingBlocks(): ArrayList<FallingBlock>{
        return fallingBlocks
    }

    fun addFallingBlock(fallingBlock: FallingBlock){
        fallingBlocks.add(fallingBlock)
    }

    fun removeFallingBlock(fallingBlock: FallingBlock){
        fallingBlocks.remove(fallingBlock)
    }

    fun getDroppedItems(): ArrayList<Item>{
        return droppedItems
    }

    fun addDroppedItem(item: Item){
        droppedItems.add(item)
    }

    fun removeDroppedItem(item: Item){
        droppedItems.remove(item)
    }

}