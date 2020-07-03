package bloxorz

import bloxorz.BloxorzGame.Action.*
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGrid.Grid
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileType.Missing
import search.GraphSearch


object BloxorzGame {

    enum class Orientation {
        X, Y, Z
    }

    enum class Action(val code: Char) {
        Up('U'), Down('D'), Left('L'), Right('R'), Start('S')
    }

    data class Block(val location: Location, val orientation: Orientation, val height: Int)

    data class State(val block: Block, val action: Action)

    data class Rule(val type: Type, val subject: Location, val `object`: Location) {
        enum class Type {
            WeakToggle, StrongToggle, WeakClose, StrongClose
        }
    }

    fun generateMoves(grid: Grid, v: State): List<GraphSearch.Edge<State>> {

        return Action.values()
            .filter { it != Start }
            .map { generateState(it, v) }
            .filter { isLegal(grid, it) }
            .map { GraphSearch.Edge(1, it) }
    }

    fun generateState(action: Action, state: State): State {

        val x = state.block.location.x
        val y = state.block.location.y
        val blockHeight = state.block.height
        val blockWidth = 1
        val orientation = state.block.orientation

        return when (Pair(action, orientation)) {
            Pair(Up, X) -> State(Block(Location(x, y + blockWidth), X, blockHeight), action)
            Pair(Up, Y) -> State(Block(Location(x, y + blockHeight), Z, blockHeight), action)
            Pair(Up, Z) -> State(Block(Location(x, y + blockWidth), Y, blockHeight), action)
            Pair(Down, X) -> State(Block(Location(x, y - blockWidth), X, blockHeight), action)
            Pair(Down, Y) -> State(Block(Location(x, y - blockWidth), Z, blockHeight), action)
            Pair(Down, Z) -> State(Block(Location(x, y - blockHeight), Y, blockHeight), action)
            Pair(Left, X) -> State(Block(Location(x - blockWidth, y), Z, blockHeight), action)
            Pair(Left, Y) -> State(Block(Location(x - blockWidth, y), Y, blockHeight), action)
            Pair(Left, Z) -> State(Block(Location(x - blockHeight, y), X, blockHeight), action)
            Pair(Right, X) -> State(Block(Location(x + blockHeight, y), Z, blockHeight), action)
            Pair(Right, Y) -> State(Block(Location(x + blockWidth, y), Y, blockHeight), action)
            Pair(Right, Z) -> State(Block(Location(x + blockWidth, y), X, blockHeight), action)
            else -> throw RuntimeException("Invalid action $action in orientation $orientation")
        }
    }

    fun isLegal(grid: Grid, state: State): Boolean {

        fun isOffGrid(loc: Location) = loc.x < 0 || loc.y < 0 || loc.x >= grid.width || loc.y >= grid.height

        fun isTileMissing(grid: Grid, it: Location) = grid[it.x, it.y].type == Missing

        val location = state.block.location
        val orientation = state.block.orientation
        val height = state.block.height

        val locations= when (Pair(orientation, height)) {
            Pair(X,2) -> listOf(location, Location(location.x + 1, location.y))
            Pair(Y,2) -> listOf(location, Location(location.x, location.y + 1))
            else -> listOf(location)
        }

        return locations.none{ isOffGrid(it) || isTileMissing(grid, it) }

    }

    fun isAtSink(v: State, grid: Grid) = v.block.location == grid.sinkLocation() && v.block.orientation == Z


}
