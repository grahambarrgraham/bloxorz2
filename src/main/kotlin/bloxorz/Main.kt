package bloxorz

import bloxorz.Formatting.condensedFormat
import bloxorz.Grid.load
import bloxorz.Search.approximateStraightLineDistanceToLocation
import bloxorz.Search.shortestPathForward
import search.GraphSearch.Path

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        runAllLevels((1..33).filterNot { listOf(23).contains(it) })
        //runAllLevels((1..33).filterNot { listOf(23).contains(it) }, detailedOutputMonitor)
    }

    private fun runAllLevels(levels: List<Int>, monitor: (Path<Game.State, Game.Action>) -> Unit = {}) {
        var totalCost = 0
        var completedLevels = 0

        levels.asSequence().forEach {
            try {
                var millis = 0L
                val grid: Grid.Grid = load("/level${it}.txt")
                val searchResult =
                    measureTimeMillis({ time -> millis = time }) { shortestPathForward(
                        grid,
                        heuristic = { approximateStraightLineDistanceToLocation(it, grid.sinkLocation()) },
                        monitor = monitor) }
                val condensedFormat = condensedFormat(searchResult.path)
                println("level $it : ${searchResult.path.history.size} moves : $condensedFormat took $millis ms with ${searchResult.metrics.expansions} expansions")

                totalCost += searchResult.path.history.size
                completedLevels += 1
            } catch (e: Exception) {
                println("level $it failed with $e")
            }
        }

        println("Summary : completed $completedLevels of 33 levels, total moves : $totalCost")
    }

    private val detailedOutputMonitor = { path: Path<Game.State, Game.Action> ->
        val edge = path.history.lastOrNull()
        println("${path.cost}, ${path.history.size}, ${edge?.action?.toString()?.get(0)}, ${edge?.destination}")
    }

    fun playback(
        grid: Grid.Grid,
        actions: List<Game.Action>
    ): Game.State {
        var state = Game.initialState(grid)

        actions.forEach {
            if (!Game.isLegal(grid, state)) {
                throw Search.IllegalAction("$it was not legal at $state")
            }
            state = Game.generateNextState(grid, it, state)
        }
        return state
    }

    private fun playActionList(grid: Grid.Grid, actions: List<Game.Action>): Boolean =
        Game.isAtSink(grid, playback(grid, actions))

    private inline fun <T> measureTimeMillis(
        loggingFunction: (Long) -> Unit,
        function: () -> T
    ): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }

}