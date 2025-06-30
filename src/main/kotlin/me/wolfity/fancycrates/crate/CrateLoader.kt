package me.wolfity.fancycrates.crate

import me.wolfity.developmentutil.util.style
import me.wolfity.fancycrates.plugin
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.yaml.snakeyaml.Yaml
import java.io.File

class CrateLoader {

    fun loadCratesFromFolder(): List<CrateConfig> {
        val cratesDir = File(plugin.dataFolder, "crates")
        if (!cratesDir.exists()) cratesDir.mkdirs()

        val yaml = Yaml()
        val crateConfigs = mutableListOf<CrateConfig>()
        val seenCrateIds = mutableSetOf<String>()

        cratesDir.listFiles { file -> file.extension == "yml" }?.forEach { file ->
            val data = yaml.load<Map<String, Map<String, Any>>>(file.reader())
            data.forEach { (crateName, crateSection) ->
                val crateId = crateSection["crate-name"].toString()

                if (!seenCrateIds.add(crateId)) {
                    throw IllegalArgumentException("Duplicate crate-name '$crateId' found in file '${file.name}'. Crate IDs must be unique.")
                }

                val crateDisplayName: String = crateSection["crate-display-name"].toString()
                val crateHologramYLevel = crateSection["crate-hologram-y-level"] as Double? ?: 2.0
                val cratePreviewGuiTitle = crateSection["crate-preview-gui-title"]?.toString() ?: crateDisplayName
                val crateHologramTitle = crateSection["crate-hologram-text"]?.toString() ?: crateDisplayName
                val crateBlock = (Material.valueOf(crateSection["crate-block"].toString()))
                val rewardsSection = crateSection["crate-rewards"] as? Map<*, *> ?: return@forEach
                val animationId = crateSection["animation-id"] as? String
                    ?: throw IllegalArgumentException("No Animation ID Set! Set 'animation-id' to ${plugin.crateAnimationRegistry.getAll()}")
                val crateKeyMaterial = crateSection["key-item"] as String
                val rewards = rewardsSection.mapNotNull { (_, rewardDataRaw) ->
                    val rewardData = rewardDataRaw as? Map<*, *> ?: return@mapNotNull null
                    val material = Material.matchMaterial(rewardData["item"] as? String ?: "") ?: return@mapNotNull null
                    val amount = (rewardData["amount"] as? Int) ?: 1
                    val weight = (rewardData["weight"] as? Int) ?: 1
                    val rewardCommand: String? = rewardData["reward-command"]?.toString()
                    val displayName = rewardData["displayName"] as? String
                    val lore = rewardData["lore"] as? List<String>

                    val enchantsMap = mutableMapOf<Enchantment, Int>()
                    val enchantsSection = rewardData["enchants"] as? Map<*, *>
                    enchantsSection?.forEach { (_, value) ->
                        val enchantData = value as? Map<*, *> ?: return@forEach
                        val enchantName = enchantData["type"] as? String ?: return@forEach
                        val level = (enchantData["level"] as? Int) ?: return@forEach
                        val enchant = Enchantment.getByName(enchantName.uppercase()) ?: return@forEach
                        enchantsMap[enchant] = level
                    }

                    CrateRewardConfig(
                        item = material,
                        amount = amount,
                        weight = weight,
                        command = rewardCommand,
                        displayName = displayName,
                        lore = lore,
                        enchants = enchantsMap.takeIf { it.isNotEmpty() }
                    )
                }
                crateConfigs.add(
                    CrateConfig(
                        crateId,
                        crateDisplayName,
                        Material.valueOf(crateKeyMaterial),
                        animationId,
                        crateBlock,
                        rewards,
                        style(crateHologramTitle),
                        crateHologramYLevel,
                        style(cratePreviewGuiTitle)
                    )
                )
            }
        }
        return crateConfigs
    }
}