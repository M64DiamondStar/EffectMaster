package me.m64diamondstar.effectmaster.shows.utils

enum class ParameterType {

    DELAY{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The amount of ticks the effect has to wait before it gets played.")
            list.add("If the delay is for example set to 60, the effect will be displayed 3 seconds after the show started.")
            return list
        }

        override fun getExample(): String {
            return "40"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    DURATION{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Some block effects get played for a while.")
            list.add("You can set the duration of the effect by changing this parameter.")
            list.add("Remember that time is always set in ticks (20 ticks = 1 second).")
            return list
        }

        override fun getExample(): String {
            return "40"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    LENGTH{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Some particle or entity effects get played for a while.")
            list.add("You can set the duration of the effect by changing this parameter.")
            list.add("Remember that time is always set in ticks (20 ticks = 1 second).")
            return list
        }

        override fun getExample(): String {
            return "40"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    LIFETIME{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("How long an effect or a part of an effect should stay.")
            list.add("This is for example used in the Item Fountain effect to configure")
            list.add("for how long the items should stay visible.")
            list.add("Remember that time is always set in ticks (20 ticks = 1 second).")
            return list
        }

        override fun getExample(): String {
            return "60"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    LOCATION{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The location where the effect should be played.")
            list.add("This has to be written in the format 'world_name, x, y, z.'.")
            return list
        }

        override fun getExample(): String {
            return "world, 54.5, 32, 76.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.LOCATION
        }
    },
    FROMLOCATION{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Some effects have 2 locations. This is the first location.")
            list.add("This has to be written in the format 'world_name, x, y, z.'.")
            return list
        }

        override fun getExample(): String {
            return "world, 54.5, 32, 76.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.LOCATION
        }
    },
    TOLOCATION{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Some effects have 2 locations. This is the second location.")
            list.add("This has to be written in the format 'world_name, x, y, z.'.")
            return list
        }

        override fun getExample(): String {
            return "world, 54.5, 35, 76.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.LOCATION
        }
    },
    BLOCK{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The block used in the effect.")
            list.add("This can also be used with the ITEM_CRACK, BLOCK_CRACK and BLOCK_DUST particle.")
            list.add("A list of materials can be found here:")
            list.add("https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
            return list
        }

        override fun getExample(): String {
            return "STONE"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.MATERIAL
        }
    },
    REPLACING{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The replacing block.")
            list.add("Used for the replace fill effect. This block will be replaced")
            list.add("with the 'Block' parameter.")
            list.add("A list of materials can be found here:")
            list.add("https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
            return list
        }

        override fun getExample(): String {
            return "COBBLESTONE"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.MATERIAL
        }
    },
    MATERIAL{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The material used in the effect.")
            list.add("A list of materials can be found here:")
            list.add("https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html")
            return list
        }

        override fun getExample(): String {
            return "DIAMOND_SWORD"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.MATERIAL
        }
    },
    BLOCKDATA{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Parameter used to set specific block data.")
            list.add("For example an open fence, a waterlogged slab, ...")
            list.add("Use [] if you don't want any additional data.")
            list.add("Here is some extra information:")
            list.add("https://minecraft.fandom.com/wiki/Block_states")
            return list
        }

        override fun getExample(): String {
            return "[waterlogged=true,axis=z]"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.STRING
        }
    },
    CUSTOMMODELDATA{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("If you use custom models you can apply the")
            list.add("custom model data with this parameter.")
            return list
        }

        override fun getExample(): String {
            return "3"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    PARTICLE{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The particle type.")
            list.add("The list of particles can be found here:")
            list.add("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html")
            return list
        }

        override fun getExample(): String {
            return "FLAME"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.PARTICLE
        }
    },
    COLOR{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("When using a color-able particle (REDSTONE, SPELL_MOB and SPELL_MOB_AMBIENT),")
            list.add("the color can be set with this parameter.")
            list.add("The format should be 'red, green, blue'")
            return list
        }

        override fun getExample(): String {
            return "212, 54, 132"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.COLOR
        }
    },
    AMOUNT{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Particle effects can be displayed with a specific amount.")
            list.add("Use this parameter to set how much particles should be spawned.")
            return list
        }

        override fun getExample(): String {
            return "200"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    DX{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("This is the delta x value for a particle.")
            list.add("Delta specifies the dimensions (in number of blocks) for each")
            list.add("dimension of the particle effect, with x y z in the center.")
            list.add("Each coordinate specifies the number of blocks from")
            list.add("the center that the particles will appear.")
            return list
        }

        override fun getExample(): String {
            return "0.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    DY{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("This is the delta y value for a particle.")
            list.add("Delta specifies the dimensions (in number of blocks) for each")
            list.add("dimension of the particle effect, with x y z in the center.")
            list.add("Each coordinate specifies the number of blocks from")
            list.add("the center that the particles will appear.")
            return list
        }

        override fun getExample(): String {
            return "0.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    DZ{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("This is the delta z value for a particle.")
            list.add("Delta specifies the dimensions (in number of blocks) for each")
            list.add("dimension of the particle effect, with x y z in the center.")
            list.add("Each coordinate specifies the number of blocks from")
            list.add("the center that the particles will appear.")
            return list
        }

        override fun getExample(): String {
            return "0.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    FORCE{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Sets whether the particles should be forced to the player.")
            list.add("If this is enabled, the player will be able to see the ")
            list.add("particle from much further.")
            return list
        }

        override fun getExample(): String {
            return "false"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    VELOCITY{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Sets the velocity of falling blocks.")
            list.add("This is used to launch falling blocks in a specific direction.")
            list.add("Don't set these values too high (I would say around a maximum of 10).")
            return list
        }

        override fun getExample(): String {
            return "0, 1, 0"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.VECTOR
        }
    },
    RANDOMIZER{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("This randomizes the value of the velocity a bit.")
            list.add("The higher the randomizer, the more random the velocity.")
            list.add("For fountains I suggest putting it from 0.1 to 0.5.")
            return list
        }

        override fun getExample(): String {
            return "0.25"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    REAL{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Sets whether the block(s) placed should be real.")
            list.add("I suggest putting this to false because it gives less lag on the server")
            list.add("when using big amounts and it makes sure that you can't mess up if you")
            list.add("enter coordinates wrongly.")
            return list
        }

        override fun getExample(): String {
            return "false"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    NAME{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Used for animatronics and animatronic groups.")
            list.add("Set this parameter to the animatronic (group) you would like to play.")
            return list
        }

        override fun getExample(): String {
            return "random_name"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.ALL
        }
    },
    COMMAND{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The command to run without a / in front. (Only add a /")
            list.add("when you want to do a WorldEdit command).")
            return list
        }

        override fun getExample(): String {
            return "say Hey there!"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.ALL
        }
    },
    SPEED{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Used for effects with a specific speed like particle line.")
            list.add("The higher the value, the faster it goes.")
            return list
        }

        override fun getExample(): String {
            return "1.5"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    STARTUP{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Used to slowly start up a particle emitter.")
            list.add("This parameters sets the amount of time it takes to")
            list.add("let the emitter effect play the full amount.")
            return list
        }

        override fun getExample(): String {
            return "40"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    COLORS{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("A list of colors to use in the effect.")
            list.add("Used for the firework effect.")
            return list
        }

        override fun getExample(): String {
            return "#ffffff, #828282, #000000"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.COLOR_LIST
        }
    },
    FADECOLORS{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("A list of fade colors used for")
            list.add("the firework effect.")
            return list
        }

        override fun getExample(): String {
            return "#ffffff, #828282, #000000"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.COLOR_LIST
        }
    },
    FIREWORKSHAPE{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The shape used for the firework effect.")
            list.add("Possible options are: ")
            list.add("BALL, BALL_LARGE, BURST, CREEPER, STAR")
            return list
        }

        override fun getExample(): String {
            return "BALL"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.FIREWORK_SHAPE
        }
    },
    SHOTATANGLE{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Makes sure firework flies in a straight line")
            list.add("when using the Velocity parameter for the")
            list.add("firework effect. You need to set the velocity")
            list.add("or else it won't move.")
            return list
        }

        override fun getExample(): String {
            return "false"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    FLICKER{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Flicker option for firework effect.")
            return list
        }

        override fun getExample(): String {
            return "false"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    TRAIL{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Trail option for firework effect.")
            return list
        }

        override fun getExample(): String {
            return "false"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    POWER{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Set the power of an effect. Currently")
            list.add("only used for the firework effect.")
            return list
        }

        override fun getExample(): String {
            return "2"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    FREQUENCY{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The frequency of an effect, this is currently")
            list.add("only used in some animation effects and it")
            list.add("configures how many times an entity/effect")
            list.add("should spawn. Check the wiki for a better")
            list.add("explanation.")
            return list
        }

        override fun getExample(): String {
            return "10"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    PATH{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("A path of locations, used by the path effects.")
            list.add("(ParticlePath, FountainPath, ...)")
            return list
        }

        override fun getExample(): String {
            return "world, 4.98, 6.17, 3.5; 1.50, 3.15, 9.75"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.PATH
        }
    },
    SMOOTH{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Should the animation play smooth?")
            return list
        }

        override fun getExample(): String {
            return "true"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.BOOLEAN
        }
    },
    CATEGORY{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The category of a show.")
            return list
        }

        override fun getExample(): String {
            return "my_category"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.STRING
        }
    },
    SHOW{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The name of a show")
            return list
        }

        override fun getExample(): String {
            return "my_show"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.STRING
        }
    },
    FROM{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("From which effect ID the show should start.")
            list.add("Enter 0 to play it from the first effect.")
            return list
        }

        override fun getExample(): String {
            return "4"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    },
    SOUND{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The name of a sound")
            return list
        }

        override fun getExample(): String {
            return "minecraft:entity.pig.ambient"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.STRING
        }
    },
    SOUNDSOURCE{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The source of the sound. This can be ambient, block, ")
            list.add("hostile, master, music, neutral, player, record, voice or weather.")
            return list
        }

        override fun getExample(): String {
            return "4"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.SOUNDSOURCE
        }
    },
    VOLUME{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The volume of a sound.")
            list.add("The volume must be at least a value of 0.0.")
            list.add("The higher the value, the larger the audible sphere and")
            list.add("the further away the sound effect can be heard.")
            return list
        }

        override fun getExample(): String {
            return "1"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    PITCH{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("The pitch of a sound effect.")
            list.add("It can be a value between 0.0 and 2.0.")
            list.add("The higher the value, the higher the pitch.")
            return list
        }

        override fun getExample(): String {
            return "1"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.DOUBLE
        }
    },
    SELECTOR{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("A Minecraft selector. @e and @s do not work")
            list.add("because it's pointless.")
            list.add("Find more info about it here:")
            list.add("https://minecraft.wiki/w/Target_selectors")
            return list
        }

        override fun getExample(): String {
            return "@p[distance=..5]"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.SELECTOR
        }
    },
    INTERVAL{
        override fun getInfo(): List<String> {
            val list = ArrayList<String>()
            list.add("Every how many ticks")
            list.add("this effect should play.")
            return list
        }

        override fun getExample(): String {
            return "1"
        }

        override fun getFormat(): ParameterFormatType {
            return ParameterFormatType.INT
        }
    };


    abstract fun getInfo(): List<String>

    abstract fun getExample(): String

    abstract fun getFormat(): ParameterFormatType

}