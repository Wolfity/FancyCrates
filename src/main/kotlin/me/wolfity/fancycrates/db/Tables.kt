package me.wolfity.fancycrates.db

import org.jetbrains.exposed.sql.Table

object CrateLocations : Table("crate_locations") {
    val id = uuid("id").autoGenerate()

    val crateId = varchar("crate_id", 32)
    val world = varchar("world", 100)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val blockFace = varchar("block_face", 16).nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}