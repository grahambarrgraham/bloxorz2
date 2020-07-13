package bloxorz

import bloxorz.Formatting.condensedFormat
import bloxorz.Grid.load
import bloxorz.Search.allPathsForward
import bloxorz.Search.shortestPathForward
import search.GraphSearch.Path

object Main {

    private val bloxorz1Actions = loadBloxorz1Actions()

    @JvmStatic
    fun main(args: Array<String>) {
        //println("Optimal : ${postedCounts.sum()}")
        runAllLevels((1..33).filterNot { listOf(23).contains(it) })
        //runAllLevels((1..33).filterNot { listOf(23).contains(it) }, detailedOutputMonitor)
    }

    private fun runAllLevels(levels: List<Int>, monitor: (Path<Game.State, Game.Action>) -> Unit = {}) {
        var allMoves = mutableListOf<Game.Action>()
        var completedLevels = 0

        levels.asSequence().forEach {
            try {
                var millis = 0L
                val grid: Grid.Grid = load("/level${it}.txt")
                val searchResult =
                    measureTimeMillis({ time -> millis = time }) {
                        shortestPathForward(
                            grid,
                            monitor = monitor
                        )
                    }
                allMoves.addAll(printResult(searchResult, it, bloxorz1Actions, millis))
                completedLevels += 1
            } catch (e: Exception) {
                println("level $it failed with $e")
            }
        }

        val switchBlockCount = allMoves.count { it == Game.Action.SwitchBlock }

        println(
            "Summary : completed $completedLevels of 33 levels, total moves (with/without) switchblocks : " +
                    "(${allMoves.count()}/${allMoves.count() - switchBlockCount})."
        )
    }

    fun searchForwardsThenBackwards(level: Int): Pair<Search.ShortestPathResult, Search.ShortestPathResult> =
        searchForwardsThenBackwards(load("/level${level}.txt"))

    private fun searchForwardsThenBackwards(grid: Grid.Grid): Pair<Search.ShortestPathResult, Search.ShortestPathResult> {
        val shortestPathForward = shortestPathForward(grid)
        val shortestPathBackward = Search.shortestPathBackward(
            grid,
            source = shortestPathForward.path.history.last().destination,
            sink = Game.initialState(grid)
        )
        return Pair(shortestPathForward, shortestPathBackward)
    }

    fun allRoutesForLevel(level: Int, monitor: (Path<Game.State, Game.Action>) -> Unit = {}) {


        val grid: Grid.Grid = load("/level${level}.txt")

        val allPaths = allPathsForward(grid, monitor = monitor)

        var millis = System.currentTimeMillis()
        allPaths
            .forEach { result ->
                printResult(result, level, bloxorz1Actions, System.currentTimeMillis() - millis)
                millis = System.currentTimeMillis()
            }
    }

    private fun printResult(
        searchResult: Search.ShortestPathResult,
        it: Int,
        bloxorz1Actions: List<List<Game.Action>>,
        millis: Long
    ): List<Game.Action> {
        val condensedFormat = condensedFormat(searchResult.path)
        val actions = searchResult.path.history.map { it.action }
        val countWithoutSwitchBlocks = actions.count { it != Game.Action.SwitchBlock }
        val switchCount = actions.count { it == Game.Action.SwitchBlock }
        val diffToPostedExSwitchBlocks = countWithoutSwitchBlocks - postedCounts[it - 1]
        val bloxorz1Count = bloxorz1Actions[it - 1].count()
        val bloxorz1WithoutSwitchBlocksCount = bloxorz1Actions[it - 1].count { it != Game.Action.SwitchBlock }
        val diffToBloxorz1ExSwitchBlocks = countWithoutSwitchBlocks - bloxorz1WithoutSwitchBlocksCount
        val diffToPosted = actions.count() - postedCounts[it - 1]
        val diffToBloxorz = actions.count() - bloxorz1Count
        println(
            "level $it : moves (with/without) switch blocks (${actions.count()}/$countWithoutSwitchBlocks) " +
                    "diff from posted ($diffToPosted/$diffToPostedExSwitchBlocks), " +
                    "diff from bloxorz1 ($diffToBloxorz/$diffToBloxorz1ExSwitchBlocks}) " +
                    "$condensedFormat took $millis ms with ${searchResult.metrics.expansions} expansions"
        )
        return actions
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

    private inline fun <T> measureTimeMillis(
        loggingFunction: (Long) -> Unit,
        function: () -> T
    ): T {

        val startTime = System.currentTimeMillis()
        val result: T = function.invoke()
        loggingFunction.invoke(System.currentTimeMillis() - startTime)

        return result
    }

    private fun loadBloxorz1Actions() =
        Scratch.javaClass.getResource("/bloxorz1-result.txt")
            .readText()
            .lines()
            .map {
                Formatting.expandToActionList(it)
            }

    val postedCounts = listOf<Int>(
        7,
        17,
        19,
        28,
        33,
        35,
        44,
        10,
        24,
        57,
        47,
        65,
        46,
        67,
        57,
        28,
        106,
        85,
        67,
        56,
        71,
        65,
        75,
        57,
        55,
        104,
        71,
        100,
        104,
        114,
        91,
        129,
        65
    )

}