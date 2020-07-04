package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.Start
import bloxorz.BloxorzGame.generateMoves
import bloxorz.BloxorzGame.generateNextState
import bloxorz.BloxorzGame.initialState
import bloxorz.BloxorzGame.isAtSink
import bloxorz.BloxorzGame.isLegal
import search.GraphSearch
import search.GraphSearch.Path
import java.util.*

object BloxorzSearch {

    class IllegalAction(message: String) : Exception(message)

    fun shortestPath(filename: String): Path<BloxorzGame.State> {

        val grid = BloxorzGrid.load(filename)
        val initialState = initialState(grid)

        return GraphSearch.shortestPath(initialState,
            { v -> isAtSink(v, grid) },
            { v -> generateMoves(grid, v) }
        )
    }

    fun allPaths(filename: String): Sequence<Path<BloxorzGame.State>> {

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
                return throw IllegalAction("$it was not legal at $state")
            }
            state = generateNextState(grid, it, state)
        }

        return isAtSink(state, grid)
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
            .map {
                expandRepeated(it)
            }.flatten()
    }


    fun condensedFormat(path: Path<BloxorzGame.State>): String {
        return condensedFormat(path.history.map {it.destination.action.code})
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

    fun detailedFormat(path: Path<BloxorzGame.State>): String {
        return path.history.map {
            "${it.destination.action.code}" +
                    "->(${it.destination.activeBlock.location.x},${it.destination.activeBlock.location.y})" +
                    "${it.destination.activeBlock.orientation}"
        }
            .toString()
    }

    @JvmStatic
    fun main(args: Array<String>) {

        var totalCost = 0
        var completedLevels = 0

        (1..33).asSequence().forEach {
            try {
                val path = shortestPath("/level${it}.txt")
                println("level $it : ${path.cost} moves : ${condensedFormat(path)}")
                totalCost += path.cost
                completedLevels += 1
            } catch (e: Exception) {
                println("level $it failed with $e")
            }
        }

        println("Summary : completed $completedLevels of 33 levels, total moves : $totalCost")

    }


}
