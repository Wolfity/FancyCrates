package me.wolfity.fancycrates

import me.wolfity.developmentutil.ext.registerListener
import me.wolfity.developmentutil.files.CustomConfig
import me.wolfity.developmentutil.misc.UpdateChecker
import me.wolfity.developmentutil.player.PlayerManager
import me.wolfity.fancycrates.animation.AnimationRegistry
import me.wolfity.fancycrates.commands.CrateCommands
import me.wolfity.fancycrates.commands.params.*
import me.wolfity.fancycrates.crate.CrateLoader
import me.wolfity.fancycrates.crate.CrateManager
import me.wolfity.fancycrates.db.DatabaseManager
import me.wolfity.fancycrates.listeners.CrateListeners
import me.wolfity.fancycrates.state.PlayerStateManager
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.Lamp
import revxrsal.commands.bukkit.BukkitLamp
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import java.io.File

lateinit var plugin: FancyCrates

class FancyCrates : JavaPlugin() {

    companion object {
        const val RESOURCE_ID = 126171
    }

    private lateinit var _crateAnimationRegistry: AnimationRegistry
    private lateinit var _crateManager: CrateManager
    private lateinit var _playerStateManager: PlayerStateManager

    private lateinit var crateLoader: CrateLoader
    private lateinit var lamp: Lamp<BukkitCommandActor>
    lateinit var dbConfig: CustomConfig
    private lateinit var placeholderCrate: CustomConfig

    val crateAnimationRegistry: AnimationRegistry
        get() = _crateAnimationRegistry

    val playerStateManager: PlayerStateManager
        get() = _playerStateManager

    val crateManager: CrateManager
        get() = _crateManager

    private lateinit var updateChecker: UpdateChecker

    override fun onEnable() {
        plugin = this

        this.updateChecker = UpdateChecker(this, plugin.description.version, RESOURCE_ID)
        updateCheck()

        loadFiles()
        DatabaseManager.init()

        registerManagers()

        setupLamp()

        registerCommands()
        registerListeners()
    }

    override fun onDisable() {

    }

    private fun loadFiles() {
        saveDefaultConfig()
        this.dbConfig = CustomConfig(this, "db.yml")
        handlePlaceholderCrate()
    }

    private fun registerManagers() {
        this.crateLoader = CrateLoader()
        this._crateAnimationRegistry = AnimationRegistry()
        this._crateManager = CrateManager(crateLoader.loadCratesFromFolder())
        this._playerStateManager = PlayerStateManager()
    }

    private fun registerListeners() {
        CrateListeners().registerListener(this)
        updateChecker.registerListener(this)
    }

    private fun registerCommands() {
        lamp.register(CrateCommands())
    }

    private fun setupLamp() {
        this.lamp = BukkitLamp.builder(this)
            .parameterTypes {
                it.addParameterType(UserCommandParameter::class.java, UserParameterType())
                it.addParameterType(AnimationParameter::class.java, AnimationParameterType())
                it.addParameterType(CrateParameter::class.java, CrateParameterType())
            }
            .build()
    }

    private fun handlePlaceholderCrate() {
        val cratesDir = File(dataFolder, "crates")
        if (!cratesDir.exists()) cratesDir.mkdirs()

        val ymlFiles = cratesDir.listFiles { file -> file.extension == "yml" }
        if (ymlFiles == null || ymlFiles.isEmpty()) {

            this.placeholderCrate = CustomConfig(this, "crates/placeholder.yml")
        }
    }

    private fun updateCheck() {
        updateChecker.getVersion(
            this,
            RESOURCE_ID
        ) { version ->
            if (this.description.version == version) {
                logger.info("There is not a new update available.");
            } else {
                logger.info("There is a new update available for ${plugin.description.name}");
            }
        }
    }
}
