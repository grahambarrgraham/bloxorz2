package bloxorz

import bloxorz.BloxorzGame.Rule
import bloxorz.BloxorzGrid.Grid.GridHasNoTileWithTag
import bloxorz.BloxorzGrid.Grid.UnknownRuleType
import bloxorz.BloxorzGrid.TileType.*
import java.lang.StringBuilder

object BloxorzGrid {

    data class Location(val x: Int, val y: Int)

    enum class TileType(val code: String) {
        Missing("X"), Normal("n"), Source("s"), Sink("S")
    }

    data class Tile(val type: TileType, val tag: String)

    data class Grid(val tiles: List<List<Tile>>, val rules: List<Rule>) {

        class GridHasNoTileWithTag(tag: String) : Exception(tag)
        class GridHasNoTileOfType(type: TileType) : Exception(type.toString())
        class UnknownRuleType(message: String) : Exception(message)

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
            throw GridHasNoTileOfType(tileType)
        }
    }

    fun load(filename: String): Grid {

        val parts = javaClass.getResource(filename)
            .readText()
            .split("\\s+-+\\s+".toRegex())

        val tiles = parts[0].lines().reversed().map(this::tile)
        val rules = if (parts.size > 1) parts[1].lines().map {it -> rule(it, tiles)} else listOf()
        return Grid(tiles, rules)
    }

    private fun rule(line: String, tiles: List<List<Tile>>): Rule {

        fun location(tag: String): Location {
            for (y in tiles.indices) {
                for (x in tiles[0].indices) {
                    if (tiles[y][x].tag == tag) return Location(x, y)
                }
            }
            throw GridHasNoTileWithTag(tag)
        }

        val parts = line.split(" ")
        val subjectTag = parts[0]
        val subjectLocation = location(subjectTag)
        val objectTag = parts[2]
        val objectLocation = location(objectTag)
        val verb = parts[1].trim()
        val tileTypeIndicator = subjectTag[0]

        val type = when (Pair(verb, tileTypeIndicator)) {
            Pair("toggles",'W') -> Rule.Type.WeakToggle
            Pair("toggles",'S') -> Rule.Type.StrongToggle
            Pair("closes",'W') -> Rule.Type.WeakClose
            Pair("closes",'S') -> Rule.Type.StrongClose
            else -> throw UnknownRuleType(verb)
        }
        return Rule(type, subjectLocation, objectLocation)
    }

    private fun tile(line: String): List<Tile> {
        return line.split("\\s+".toRegex()).map {
            when (it) {
                "p" -> Tile(Normal, it)
                "s" -> Tile(Source, it)
                "e" -> Tile(Sink, it)
                else -> Tile(Missing, it)
            }
        }
    }
}