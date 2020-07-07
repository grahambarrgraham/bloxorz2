package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.*
import bloxorz.BloxorzGame.State
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

    data class SearchMetrics(val expansions: Int)
    data class ShortestPathResult(val path: Path<State, Action>, val metrics: SearchMetrics)

    fun shortestPath(filename: String): ShortestPathResult {

        val grid = BloxorzGrid.load(filename)
        val initialState = initialState(grid)
        var expansions = 0

        val shortestPath = GraphSearch.shortestPath(initialState,
            { v -> isAtSink(v, grid) },
            { v -> expansions++; generateMoves(grid, v) }
        )

        return ShortestPathResult(shortestPath, SearchMetrics(expansions))
    }

    fun allPaths(filename: String): Sequence<Path<State, Action>> {

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
                throw IllegalAction("$it was not legal at $state")
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

    @JvmStatic
    fun main(args: Array<String>) {

        var totalCost = 0
        var completedLevels = 0
        //.filterNot { listOf(23, 26, 28).contains(it) }
        (1..33).asSequence().forEach {
            try {
                var millis = 0L
                val searchResult = measureTimeMillis({ time -> millis = time }) { shortestPath("/level${it}.txt") }
                val condensedFormat = condensedFormat(searchResult.path)
                println("level $it : ${searchResult.path.cost} moves : $condensedFormat took $millis ms with ${searchResult.metrics.expansions} expansions")

                totalCost += searchResult.path.cost
                completedLevels += 1
            } catch (e: Exception) {
                println("level $it failed with $e")
            }
        }

        println("Summary : completed $completedLevels of 33 levels, total moves : $totalCost")
    }

    inline fun <T> measureTimeMillis(
        loggingFunction: (Long) -> Unit,
        function: () -> T
    ): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }
}
