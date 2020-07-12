package bloxorz

import bloxorz.Game.Action
import bloxorz.Game.GameDirection
import bloxorz.Game.State
import bloxorz.Game.generateMoves
import bloxorz.Game.initialState
import bloxorz.Game.isAtSink
import bloxorz.Grid.Grid
import bloxorz.Grid.Location
import search.GraphSearch
import search.GraphSearch.Path
import kotlin.math.abs

object Search {

    class IllegalAction(message: String) : Exception(message)

    class SearchMetrics(var expansions: Int = 0)

    data class ShortestPathResult(val path: Path<State, Action>, val metrics: SearchMetrics)

    fun shortestPath(
        source: State,
        isSink: (State) -> Boolean,
        edges: (State) -> List<GraphSearch.Edge<State, Action>>,
        heuristic: (State) -> Int = { 0 },
        monitor: (Path<State, Action>) -> Unit = {}
    ): ShortestPathResult {
        val metrics = SearchMetrics()

        val path = GraphSearch.shortestPath(
            source,
            isSink,
            edges,
            heuristic,
            { path: Path<State, Action> -> metrics.expansions++; monitor(path) }
        )
        return ShortestPathResult(path, metrics)
    }

    fun shortestPath(
        grid: Grid,
        source: State,
        isSink: (State) -> Boolean,
        direction: GameDirection,
        heuristic: (State) -> Int = { 0 },
        monitor: (Path<State, Action>) -> Unit = {}
    ): ShortestPathResult {
        return shortestPath(source, isSink, { generateMoves(grid, it, direction) }, heuristic, monitor)
    }

    fun shortestPathForward(
        grid: Grid,
        initialState: State = initialState(grid),
        heuristic: (State) -> Int = { 0 },
        monitor: (Path<State, Action>) -> Unit = {}
    ): ShortestPathResult = shortestPath(
        initialState,
        { isAtSink(grid, it) },
        { generateMoves(grid, it, GameDirection.Forward) },
        heuristic,
        monitor
    )

    fun shortestPathBackward(
        grid: Grid,
        source: State,
        sink: State = initialState(grid),
        heuristic: (State) -> Int = { 0 },
        monitor: (Path<State, Action>) -> Unit = {}
    ): ShortestPathResult = shortestPath(
        source,
        { it == sink },
        { generateMoves(grid, it, GameDirection.Backward) },
        heuristic,
        monitor
    )

    fun approximateStraightLineDistanceToLocation(it: State, location: Location): Int {

        fun distance(location: Location?, sinkLocation: Location): Int =
            if (location == null) 0 else (abs(sinkLocation.x - location.x) + abs(sinkLocation.y - location.y)) / 2

        return (distance(it.activeBlock.location, location) +
                distance(it.inactiveBlock?.location, location)) / 2
    }


}

