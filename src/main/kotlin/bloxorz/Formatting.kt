package bloxorz

import bloxorz.Game.Action
import bloxorz.Game.Action.*
import bloxorz.Game.Orientation.*
import bloxorz.Game.State
import bloxorz.Grid.Grid
import bloxorz.Grid.Location
import bloxorz.Grid.TileState.Missing
import bloxorz.Grid.TileState.Present
import search.GraphSearch.Path
import java.util.*

object Formatting {

    fun expandToActionList(condensedFormat: String): List<Action> {

        fun expandRepeated(c: String): List<Action> {

            val substring = c.substring(1)
            val count = if (substring.isEmpty()) 1 else substring.toInt()
            val actionString = c[0]

            return (0 until count).toList().map {
                when (actionString) {
                    'U' -> Up
                    'D' -> Down
                    'L' -> Left
                    'R' -> Right
                    'S' -> SwitchBlock
                    else -> throw Exception("Unknown action : $actionString")
                }
            }
        }

        return condensedFormat.substringAfter('[')
            .substringBefore(']')
            .split(", ")
            .map {
                expandRepeated(it)
            }.flatten()
    }

    private fun code(action: Action): Char = when (action) {
        Up -> 'U'
        Down -> 'D'
        Left -> 'L'
        Right -> 'R'
        SwitchBlock -> 'S'
    }

    fun condensedFormat(path: Path<State, Action>): String {
        return condensedFormat(path.history.map { code(it.action) })
    }

    fun condensedFormat(path: List<Char>): String {

        val stack = Stack<Char>()
        val result = mutableListOf<String>()

        fun purge() {
            val instructionSymbol = stack.peek()
            if (stack.size == 1)
                result.add("$instructionSymbol")
            else
                result.add("$instructionSymbol${stack.size}")
            stack.clear()
        }

        path.forEach {
            if (!stack.empty() && it != stack.peek()) purge()
            stack.push(it)
        }

        purge()

        return result.toString()
    }

    fun detailedFormat(path: Path<State, Action>): String {
        return path.history.map {
            "${code(it.action)}" +
                    "->(${it.destination.activeBlock.location.x},${it.destination.activeBlock.location.y})" +
                    "${it.destination.activeBlock.orientation}/${it.destination.activeBlock.height}"
        }
            .toString()
    }

    fun renderGrid(grid: Grid, state: State): String {

        val location = state.activeBlock.location

        val orientation = state.activeBlock.orientation
        val renderList: List<(Location) -> Char?> = listOf(
            { it -> if (location == it && orientation == X) '*' else null },
            { it -> if (location.y == it.y && location.x + 1 == it.x && orientation == X && state.inactiveBlock == null) '*' else null },
            { it -> if (location.x == it.x && location.y + 1 == it.y && orientation == Y && state.inactiveBlock == null) '*' else null },
            { it -> if (location == it && orientation == Y) '*' else null },
            { it -> if (location == it && orientation == Z) '*' else null },
            { it -> if (state.inactiveBlock?.location == it) '+' else null },
            { it -> if (state.ruleState[it] == Present) 'P' else null },
            { it -> if (state.ruleState[it] == Missing) 'X' else null },
            { it -> if (grid[it.x, it.y].state == Missing) '.' else null },
            { it -> grid[it.x, it.y].tag[0] }
        )

        fun tileChar(l: Location) = renderList.map { it.invoke(l) }.first { it != null }

        val lines = grid.tiles.mapIndexed { y, tiles ->
            val sb = StringBuilder()
            tiles.forEachIndexed { x, _ ->
                sb.append(tileChar(Location(x, y))).append(' ')
            }
            sb.toString()
        }

        val sb = StringBuffer()
        lines.reversed().forEach {
            sb.append(it).append('\n')
        }

        return sb.toString()
    }
}

