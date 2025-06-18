package me.wolfity.fancycrates.crate

import me.wolfity.developmentutil.util.launchAsync
import me.wolfity.fancycrates.commands.CrateCommands
import me.wolfity.fancycrates.db.CrateLocations
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.addCrateHologram
import me.wolfity.fancycrates.util.runSync
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Directional
import org.bukkit.metadata.FixedMetadataValue
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class CrateManager(private val loadedCrates: List<CrateConfig>) {

    private val crateMap: MutableMap<String, CrateConfig> = mutableMapOf()

    init {
        loadCrates()
        plugin.logger.info("[Fancy Crates] A total of ${crateMap.size} crates have been loaded! (${crateMap.keys.toList()})")
    }

    fun loadCrates() {
        val crates = loadedCrates
        crateMap.clear()
        for (crate in crates) {
            crateMap[crate.name] = crate
        }
        launchAsync {
            val allCrates = getCrateLocations()

            runSync {
                allCrates.forEach { crateLoc ->
                    val crate = crateMap[crateLoc.crateId]!!
                    val location = Location(Bukkit.getWorld(crateLoc.world), crateLoc.x, crateLoc.y, crateLoc.z)

                    location.block.type = crate.crateBlock
                    location.block.setMetadata(
                        CrateCommands.CRATE_ITEM_DATA_KEY,
                        FixedMetadataValue(plugin, crate.name)
                    )

                    val block = location.block
                    val blockData = block.blockData
                    if (blockData is Directional && crateLoc.blockFace != null) {
                        blockData.facing = crateLoc.blockFace
                        block.blockData = blockData
                    }

                    crateLoc.addCrateHologram(crate)

                    block.state.update(true, true)
                }
            }
        }
    }

    private suspend fun getCrateLocations(): List<CrateLocation> {
        return newSuspendedTransaction {
            CrateLocations.selectAll().map {
                val blockFace = it[CrateLocations.blockFace]?.let {
                    runCatching { BlockFace.valueOf(it) }.getOrNull()
                }
                CrateLocation(
                    id = it[CrateLocations.id],
                    crateId = it[CrateLocations.crateId],
                    world = it[CrateLocations.world],
                    x = it[CrateLocations.x],
                    y = it[CrateLocations.y],
                    z = it[CrateLocations.z],
                    blockFace = blockFace
                )
            }
        }
    }

    suspend fun placeCrateLocation(location: Location, crateIdKey: String, face: BlockFace?): CrateLocation =
        newSuspendedTransaction {
            val id = UUID.randomUUID()
            CrateLocations.insert {
                it[this.id] = id
                it[world] = location.world.name
                it[x] = location.x
                it[y] = location.y
                it[z] = location.z
                it[crateId] = crateIdKey
                it[blockFace] = face?.name
            }

            CrateLocation(
                id = id,
                crateId = crateIdKey,
                world = location.world.name,
                x = location.x,
                y = location.y,
                z = location.z,
                blockFace = face
            )
        }


    suspend fun deleteCrateLocation(world: String, x: Double, y: Double, z: Double, crate: String): CrateLocation? =
        newSuspendedTransaction {
            val row = CrateLocations
                .selectAll().where {
                    (CrateLocations.world eq world) and
                            (CrateLocations.x eq x) and
                            (CrateLocations.y eq y) and
                            (CrateLocations.z eq z) and
                            (CrateLocations.crateId eq crate)
                }
                .firstOrNull()

            row?.let { resultRow ->
                val idToDelete = resultRow[CrateLocations.id]

                CrateLocations.deleteWhere {
                    id eq idToDelete
                }

                val blockFace = resultRow[CrateLocations.blockFace]?.let { face ->
                    runCatching { BlockFace.valueOf(face) }.getOrNull()
                }

                CrateLocation(
                    id = resultRow[CrateLocations.id],
                    crateId = resultRow[CrateLocations.crateId],
                    world = resultRow[CrateLocations.world],
                    x = resultRow[CrateLocations.x],
                    y = resultRow[CrateLocations.y],
                    z = resultRow[CrateLocations.z],
                    blockFace = blockFace
                )
            }
        }

    fun getAllCrates(): List<CrateConfig> {
        return crateMap.values.toList()
    }

    fun getCrateById(id: String): CrateConfig? {
        return crateMap[id]
    }

    fun getCrateRewards(crateId: String): List<CrateRewardConfig> {
        return crateMap[crateId]?.rewards ?: listOf()
    }
}
