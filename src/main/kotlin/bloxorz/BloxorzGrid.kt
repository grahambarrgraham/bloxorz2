package bloxorz

import bloxorz.BloxorzGrid.TileType.*
import java.lang.StringBuilder

object BloxorzGrid {

    data class Location(val x: Int, val y: Int)

    enum class TileType(val code: String) {
        Missing("X"), Normal("n"), Source("s"), Sink("S")
    }

    data class Tile(val type: TileType)

    data class Grid(val tiles: List<List<Tile>>) {

        class GridHasNoSource : Exception()

        val height = tiles.size
        val width = tiles[0].size

        operator fun get(x: Int, y: Int): Tile = tiles[y][x]

        override fun toString(): String {
            val sb = StringBuilder()
            tiles.reversed().forEach() {
                it.forEach { tile -> sb.append(tile.type.code).append(' ') }
                sb.append('\n')
            }
            return sb.toString()
        }

        fun sourceLocation(): Location {
            return location(Source)
        }

        fun sinkLocation(): Location {
            return location(Sink)
        }

        private fun location(tileType: TileType): Location {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (tiles[y][x].type == tileType) return Location(x, y)
                }
            }
            throw GridHasNoSource()
        }
    }

    fun load(filename: String): Grid {
        return Grid(
            javaClass.getResource(filename)
                .readText()
                .lines()
                .reversed()
                .map(this::tile)
        )
    }

    private fun tile(line: String): List<Tile> {
        return line.split("\\s+".toRegex()).map {
            when (it) {
                "p" -> Tile(Normal)
                "s" -> Tile(Source)
                "e" -> Tile(Sink)
                else -> Tile(Missing)
            }
        }
    }
}