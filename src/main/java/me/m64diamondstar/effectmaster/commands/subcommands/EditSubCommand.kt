package me.m64diamondstar.effectmaster.commands.subcommands

import me.m64diamondstar.effectmaster.commands.utils.DefaultResponse
import me.m64diamondstar.effectmaster.commands.utils.SubCommand
import me.m64diamondstar.effectmaster.editor.utils.EditorUtils
import me.m64diamondstar.effectmaster.shows.EffectShow
import me.m64diamondstar.effectmaster.shows.utils.Effect
import me.m64diamondstar.effectmaster.shows.utils.ParameterType
import me.m64diamondstar.effectmaster.shows.utils.ShowUtils
import me.m64diamondstar.effectmaster.utils.Colors
import me.m64diamondstar.effectmaster.utils.Prefix
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EditSubCommand: SubCommand {
    override fun getName(): String {
        return "edit"
    }

    override fun execute(sender: CommandSender, args: Array<String>) {

        if(args.size >= 5){
            if (!DefaultResponse.existsShow(sender, args))
                return

            val effectShow = EffectShow(args[1], args[2], null)

            when(args[3].lowercase()){

                "edit" -> {

                    // Not enough args
                    if(args.size < 7){
                        DefaultResponse.helpEdit(sender)
                        return
                    }

                    // Check if ID is int
                    if(args[4].toIntOrNull() == null){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The given ID is not a number."))
                        return
                    }

                    // Check if ID exists
                    if(effectShow.getEffect(args[4].toInt()) == null){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The effect with the ID ${args[4]} doesn't exist."))
                        return
                    }

                    val id = args[4].toInt()
                    val effect = effectShow.getEffect(id)!!

                    var foundMatch = false
                    for(default in effect.getDefaults()){
                        if(default.first.equals(args[5].lowercase().replaceFirstChar { it.uppercaseChar() }, ignoreCase = true))
                            foundMatch = true
                    }

                    if(!foundMatch){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The parameter " +
                                "${args[5].lowercase().replaceFirstChar { it.uppercaseChar() }} doesn't exist."))
                        return
                    }

                    val parameter = args[5].lowercase().replaceFirstChar { it.uppercaseChar() }


                    val sb = StringBuilder()

                    for (loopArgs in 6 until args.size) {
                        sb.append(args[loopArgs]).append(" ")
                    }

                    // Create val for the given value and remove the last char (because it's a space)
                    val value = "$sb".dropLast(1)

                    if(ParameterType.valueOf(parameter.uppercase()).getFormat().isPossible(value)){
                        effectShow.getEffect(id)!!.getSection().set(parameter, ParameterType.valueOf(parameter.uppercase()).getFormat().convertToFormat(value))
                        effectShow.reloadConfig()

                        sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Edited parameter."))
                        sender.sendMessage(Colors.format(Prefix.PrefixType.STANDARD.toShortString() +
                                "$parameter: $value"))
                    }else{
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The value entered ($value) is not possible."))
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "You need to enter a(n) $parameter."))
                    }

                }

                "create" -> {

                    // Not the right amount of args
                    if(args.size != 5){
                        DefaultResponse.helpEdit(sender)
                        return
                    }

                    // Check if the effect type exists
                    try{
                        Effect.Type.valueOf(args[4])
                    }catch (ex: IllegalArgumentException){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "This effect type does not exist."))
                        return
                    }

                    val effectType = Effect.Type.valueOf(args[4])

                    // Add effect
                    val id = effectShow.getMaxId() + 1
                    val effect = effectType.getTypeClass(effectShow, id)

                    // Edit defaults if sender is player (preset location ect.)
                    if(sender is Player)
                        effectShow.setDefaults(id, EditorUtils.filterDefaults(sender, effect))
                    else
                        effectShow.setDefaults(id, effect.getDefaults())

                    sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Added the effect: ${args[4].uppercase()} with the ID ${id}."))

                }

                "delete" -> {

                    // Not the right amount of args
                    if(args.size != 5){
                        DefaultResponse.helpEdit(sender)
                        return
                    }

                    // Check if ID is int
                    if(args[4].toIntOrNull() == null){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The given ID is not a number."))
                        return
                    }

                    // Check if ID exists
                    if(effectShow.getEffect(args[4].toInt()) == null){
                        sender.sendMessage(Colors.format(Prefix.PrefixType.ERROR.toString() + "The effect with the ID ${args[4]} doesn't exist."))
                        return
                    }

                    // Delete effect
                    effectShow.deleteEffect(args[4].toInt())
                    sender.sendMessage(Colors.format(Prefix.PrefixType.SUCCESS.toString() + "Deleted the effect with the ID ${args[4]}."))

                }

            }


        }else{
            DefaultResponse.helpEdit(sender)
        }

    }

    override fun getTabCompleters(sender: CommandSender, args: Array<String>): ArrayList<String> {
        val tabs = ArrayList<String>()
        if(args.size == 2)
            ShowUtils.getCategories().forEach { tabs.add(it.name) }

        if(args.size == 3)
            ShowUtils.getShows(args[1]).forEach { tabs.add(it.name) }

        if(args.size > 3){
            if(!ShowUtils.existsShow(args[1], args[2])){
                tabs.add("SHOW_DOES_NOT_EXIST")
                return tabs
            }
        }

        if(args.size == 4){
            tabs.add("create")
            tabs.add("delete")
            tabs.add("edit")
        }

        if(args.size == 5){

            val effectShow = EffectShow(args[1], args[2], null)

            when(args[3].lowercase()){

                "create" -> {
                    Effect.Type.values().forEach { tabs.add(it.toString()) }
                }

                "delete", "edit" -> {
                    for(i in 1..effectShow.getMaxId())
                        tabs.add("$i")
                }

            }
        }

        if(args.size == 6){

            val effectShow = EffectShow(args[1], args[2], null)

            if(args[3].equals("edit", ignoreCase = true)){
                // Check if ID is int
                if(args[4].toIntOrNull() == null){
                    tabs.add("NOT_A_NUMBER")
                    return tabs
                }

                // Check if ID exists
                if(effectShow.getEffect(args[4].toInt()) == null){
                    tabs.add("ID_DOES_NOT_EXIST")
                    return tabs
                }

                effectShow.getEffect(args[4].toInt())!!.getDefaults().forEach { tabs.add(it.first) }

            }
        }

        return tabs
    }
}