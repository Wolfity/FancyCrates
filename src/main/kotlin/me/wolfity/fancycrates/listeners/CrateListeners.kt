package me.wolfity.fancycrates.listeners

import eu.decentsoftware.holograms.api.DHAPI
import me.wolfity.developmentutil.ext.uuid
import me.wolfity.developmentutil.util.getData
import me.wolfity.developmentutil.util.launchAsync
import me.wolfity.developmentutil.util.sendStyled
import me.wolfity.fancycrates.commands.CrateCommands
import me.wolfity.fancycrates.gui.CratePreviewGUI
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.addCrateHologram
import me.wolfity.fancycrates.util.toCleanString
import org.bukkit.Sound
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue

class CrateListeners : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.playerStateManager.loadPlayer(event.player.uuid)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        plugin.playerStateManager.unloadPlayer(event.player.uuid)
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val action = event.action
        val clickedBlock = event.clickedBlock ?: return

        val crateId = clickedBlock.getMetadata(CrateCommands.CRATE_ITEM_DATA_KEY).firstOrNull()?.asString() ?: return
        val crateById = plugin.crateManager.getCrateById(crateId) ?: return

        val usedItem = event.item
        val keyData = usedItem?.getData(CrateCommands.CRATE_KEY_DATA_KEY)

        val isRightClick = action == Action.RIGHT_CLICK_BLOCK
        val isLeftClick = action == Action.LEFT_CLICK_BLOCK

        // Prevent non-ops from breaking crates
        if (!player.isOp && isLeftClick) {
            event.isCancelled = true
            return
        }

        // Right click w invalid item
        if (isRightClick && (usedItem == null || keyData == null)) {
            CratePreviewGUI(player, crateById)
            event.isCancelled = true
            return
        }

        // Invalid key
        if (isRightClick && keyData != crateId) {
            player.sendStyled(plugin.config.getString("wrong-key")!!)
            event.isCancelled = true
            return
        }

        // valid key
        if (isRightClick) {
            if (plugin.playerStateManager.isOpeningCrate(player.uniqueId)) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f)
                player.sendStyled(plugin.config.getString("already-opening-crate")!!)
                event.isCancelled = true
                return
            }

            event.isCancelled = true

            if (usedItem == null) return
            usedItem.amount -= 1
            val slot = player.inventory.heldItemSlot
            if (usedItem.amount <= 0) {
                player.inventory.setItem(slot, null)
            } else {
                player.inventory.setItem(slot, usedItem)
            }

            val animation = plugin.crateAnimationRegistry.get(crateById.animationId, crateById)!!
            animation.startAnimation(player, clickedBlock.location.clone().add(0.5, 1.0, 0.5))
        }
    }


    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.block
        if (event.itemInHand.getData(CrateCommands.CRATE_KEY_DATA_KEY) != null) {
            event.isCancelled = true
        }
        val blockData = event.itemInHand.getData(CrateCommands.CRATE_ITEM_DATA_KEY) ?: return
        block.setMetadata(
            CrateCommands.CRATE_ITEM_DATA_KEY,
            FixedMetadataValue(plugin, blockData)
        )

        val data = block.blockData
        val facing = (data as? Directional)?.facing

        launchAsync {
            val loc = plugin.crateManager.placeCrateLocation(event.block.location, blockData, facing)
            val crate = plugin.crateManager.getCrateById(blockData)!!
            loc.addCrateHologram(crate)
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block
        val metaData = block.getMetadata(CrateCommands.CRATE_ITEM_DATA_KEY).firstOrNull()?.asString() ?: return

        launchAsync {
            val crate = plugin.crateManager.deleteCrateLocation(
                block.world.name,
                block.x.toDouble(),
                block.y.toDouble(),
                block.z.toDouble(),
                metaData
            )!!
            DHAPI.removeHologram(crate.id.toCleanString())
        }
    }
}