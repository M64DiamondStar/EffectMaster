package me.m64diamondstar.effectmaster.update

import me.m64diamondstar.effectmaster.EffectMaster
import org.bukkit.Bukkit
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.function.Consumer

class UpdateChecker(private val plugin: EffectMaster, private val resourceId: Int) {

    fun getVersion(consumer: Consumer<String?>) {
        Bukkit.getAsyncScheduler().runNow (plugin) { _ ->
            try {
                URL("https://api.spigotmc.org/legacy/update.php?resource=$resourceId").openStream()
                    .use { inputStream ->
                        Scanner(inputStream).use { scanner ->
                            if (scanner.hasNext()) {
                                consumer.accept(scanner.next())
                            }
                        }
                    }
            } catch (exception: IOException) {
                plugin.logger.info("Unable to check for updates: " + exception.message)
            }
        }
    }

}