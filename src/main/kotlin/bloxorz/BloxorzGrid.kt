package bloxorz

import bloxorz.BloxorzGame.Rule
import bloxorz.BloxorzGrid.Grid.UnknownRuleType
import bloxorz.BloxorzGrid.TileState.Missing
import bloxorz.BloxorzGrid.TileState.Present

object BloxorzGrid {

    data class Location(val x: Int, val y: Int)

    enum class TileState {
        Missing, Present;

        fun opposite(): TileState {
            return when (this) {
                Missing -> Present
                Present -> Missing
            }
        }
    }

    class GridHasNoTileWithTag(tag: String) : Exception(tag)

    data class Tile(val state: TileState, val tag: String)

    data class Grid(val tiles: List<List<Tile>>, val rules: List<Rule>) {

        class UnknownRuleType(message: String) : Exception(message)

        val height = tiles.size
        val width = tiles[0].size

        operator fun get(x: Int, y: Int): Tile = tiles[y][x]

        override fun toString(): String {
            val sb = StringBuilder()
            tiles.reversed().forEach {
                it.forEach { tile -> sb.append(tile.tag[0]).append(' ') }
                sb.append('\n')
            }
            return sb.toString()
        }

        fun sourceLocation(): Location {
            return location("s", tiles)
        }

        fun sinkLocation(): Location {
            return location("e", tiles)
        }

        fun initialRuleState(): Map<Location, TileState> {
            return rules
                .map { it.objectLocation }
                .map { Pair(it, this[it.x, it.y].state) }
                .toMap()
        }

        fun rulesAt(loc: Location): List<Rule> {
            return rules.filter { it.subjectLocation == loc }
        }

    }

    fun load(filename: String): Grid {

        val parts = javaClass.getResource(filename)
            .readText()
            .split("\\s+-+\\s+".toRegex())

        val tiles = lines(parts[0]).reversed().map(this::tile)
        val rules = if (parts.size > 1) lines(parts[1]).flatMap { rule(it, tiles) } else listOf()

        return Grid(tiles, rules)
    }

    private fun lines(gridSection: String) = gridSection.lines().map(String::trim).filterNot(String::isBlank)

    private fun location(tag: String, tiles: List<List<Tile>>): Location {
        return locations(tag, tiles).firstOrNull() ?: throw GridHasNoTileWithTag(tag)
    }

    private fun locations(tag: String, tiles: List<List<Tile>>): Sequence<Location> {
        return sequence {
            for (y in tiles.indices) {
                for (x in tiles[0].indices) {
                    if (tiles[y][x].tag == tag) yield(Location(x, y))
                }
            }
        }
    }

    private fun rule(line: String, tiles: List<List<Tile>>): List<Rule> {

        val parts = line.split(" ")
        val subjectTag = parts[0]
        val subjectLocation = location(subjectTag, tiles)
        val objectTag = parts[2]
        val objectLocations = locations(objectTag, tiles)
        val verb = parts[1].trim()
        val tileTypeIndicator = subjectTag[0]

        val type = when (Pair(verb, tileTypeIndicator)) {
            Pair("toggles", 'W') -> Rule.Type.WeakToggle
            Pair("toggles", 'S') -> Rule.Type.StrongToggle
            Pair("closes", 'W') -> Rule.Type.WeakClose
            Pair("closes", 'S') -> Rule.Type.StrongClose
            Pair("opens", 'W') -> Rule.Type.WeakOpen
            Pair("opens", 'S') -> Rule.Type.StrongOpen

            else -> throw UnknownRuleType(verb)
        }
        return objectLocations.map { Rule(type, subjectLocation, it) }.toList()
    }

    private fun tile(line: String): List<Tile> {
        return line.trim().split("\\s+".toRegex()).map {
            when (it[0]) {
                'x' -> Tile(Missing, it)
                'X' -> Tile(Missing, it)
                else -> Tile(Present, it)
            }
        }
    }
}