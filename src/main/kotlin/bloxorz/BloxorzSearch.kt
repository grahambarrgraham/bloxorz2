package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.Start
import bloxorz.BloxorzGame.generateMoves
import bloxorz.BloxorzGame.generateState
import bloxorz.BloxorzGame.initialState
import bloxorz.BloxorzGame.isAtSink
import bloxorz.BloxorzGame.isLegal
import search.GraphSearch
import java.util.*

object BloxorzSearch {

    fun shortestPath(filename: String): GraphSearch.Path<BloxorzGame.State> {

        val grid = BloxorzGrid.load(filename)
        val initialState = initialState(grid)

        return GraphSearch.shortestPath(initialState,
            { v -> isAtSink(v, grid) },
            { v -> generateMoves(grid, v) }
        )
    }

    fun allPaths(filename: String): Sequence<GraphSearch.Path<BloxorzGame.State>> {

        val grid = BloxorzGrid.load(filename)
        val initialState = initialState(grid)

        return GraphSearch.allPaths(initialState,
            { v -> isAtSink(v, grid) },
            { v -> generateMoves(grid, v) }
        )
    }

    fun playActionList(grid: BloxorzGrid.Grid, actions: List<Action>): Boolean {

        var state = initialState(grid)

        actions.forEach {
            if (!isLegal(grid, state)) {
                return false
            }
            state = generateState(it, state)
        }

        return isAtSink (state, grid)
    }


    fun expandToActionList(condensedFormat: String): List<Action> {

        fun expandRepeated(c: String): List<Action> {

            val substring = c.substring(1)
            val count = if (substring.isEmpty()) 1 else substring.toInt()
            val actionString = c[0]

            return (0 until count).toList().map {
                when (actionString) {
                    'U' -> Action.Up
                    'D' -> Action.Down
                    'L' -> Action.Left
                    'R' -> Action.Right
                    else -> Start
                }
            }
        }

        return condensedFormat.substringAfter('[')
            .substringBefore(']')
            .split(", ")
            .map { expandRepeated(it)
            }.flatten()
    }


    fun condensedFormat(path: GraphSearch.Path<BloxorzGame.State>): String {

        val stack = Stack<Action>()
        val result = mutableListOf<String>()

        fun purge() {
            if (stack.size == 1)
                result.add("${stack.peek().code}")
            else
                result.add("${stack.peek().code}${stack.size}")
            stack.clear()
        }

        path.history.map { it.destination.action }.forEach {
            if (!stack.empty() && it != stack.peek()) purge()
            stack.push(it)
        }

        purge()

        return result.toString()
    }


    fun detailedFormat(path: GraphSearch.Path<BloxorzGame.State>): String {
        return path.history.map { "${it.destination.action.code}" +
                "->(${it.destination.block.location.x},${it.destination.block.location.y})" +
                "${it.destination.block.orientation}" }
            .toString()
    }


}
