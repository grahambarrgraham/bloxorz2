package bloxorz

import bloxorz.BloxorzGame.Action.*
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGame.Rule.Type.*
import bloxorz.BloxorzGrid.Grid
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState
import bloxorz.BloxorzGrid.TileState.Missing
import search.GraphSearch


object BloxorzGame {

    enum class Orientation {
        X, Y, Z
    }

    enum class Action(val code: Char) {
        Up('U'), Down('D'), Left('L'), Right('R'), Start('S')
    }

    data class Block(val location: Location, val orientation: Orientation, val height: Int)

    data class State(val block: Block, val action: Action, val ruleState: Map<Location, TileState>)

    data class Rule(val type: Type, val subjectLocation: Location, val objectLocation: Location) {
        enum class Type {
            WeakToggle, StrongToggle, WeakClose, StrongClose
        }
    }

    fun initialState(grid: Grid) = State(Block(grid.sourceLocation(), Z, 2), Start, grid.initialRuleState())

    fun generateMoves(grid: Grid, v: State): List<GraphSearch.Edge<State>> {

        return Action.values()
            .filter { it != Start }
            .map { generateNextState(grid, it, v) }
            .filter { isLegal(grid, it) }
            .map { GraphSearch.Edge(1, it) }
    }

    fun generateNextState(grid: Grid, action: Action, currentState: State): State {

        val x = currentState.block.location.x
        val y = currentState.block.location.y
        val blockHeight = currentState.block.height
        val blockWidth = 1
        val orientation = currentState.block.orientation

        val nextBlock = when (Pair(action, orientation)) {
            Pair(Up, X) -> Block(Location(x, y + blockWidth), X, blockHeight)
            Pair(Up, Y) -> Block(Location(x, y + blockHeight), Z, blockHeight)
            Pair(Up, Z) -> Block(Location(x, y + blockWidth), Y, blockHeight)
            Pair(Down, X) -> Block(Location(x, y - blockWidth), X, blockHeight)
            Pair(Down, Y) -> Block(Location(x, y - blockWidth), Z, blockHeight)
            Pair(Down, Z) -> Block(Location(x, y - blockHeight), Y, blockHeight)
            Pair(Left, X) -> Block(Location(x - blockWidth, y), Z, blockHeight)
            Pair(Left, Y) -> Block(Location(x - blockWidth, y), Y, blockHeight)
            Pair(Left, Z) -> Block(Location(x - blockHeight, y), X, blockHeight)
            Pair(Right, X) -> Block(Location(x + blockHeight, y), Z, blockHeight)
            Pair(Right, Y) -> Block(Location(x + blockWidth, y), Y, blockHeight)
            Pair(Right, Z) -> Block(Location(x + blockWidth, y), X, blockHeight)
            else -> throw RuntimeException("Invalid action $action in orientation $orientation")
        }

        val newRuleState = applyRules(grid, currentState.ruleState, nextBlock)

        return State(nextBlock, action, newRuleState)
    }

    private fun applyRules(grid: Grid, currentRuleState: Map<Location, TileState>, block: Block)
            : MutableMap<Location, TileState> {

        fun applyRule(rule: Rule?, currentTileState: TileState, orientation: Orientation): TileState {
            return when (rule?.type) {
                WeakToggle -> currentTileState.opposite()
                WeakClose -> Missing
                StrongClose -> if (orientation == Z) Missing else currentTileState
                StrongToggle -> if (orientation == Z) currentTileState.opposite() else currentTileState
                else -> currentTileState
            }
        }

        val applicableRules = locationsTouching(block).flatMap { grid.rulesAt(it) }
        val newRuleState = mutableMapOf<Location, TileState>()

        currentRuleState.forEach {
            val ruleAtLocation = applicableRules.find { rule -> rule.objectLocation == it.key }
            newRuleState[it.key] = applyRule(ruleAtLocation, it.value, block.orientation)
        }

        return newRuleState
    }

    fun isLegal(grid: Grid, state: State): Boolean {

        fun isOffGrid(loc: Location) = loc.x < 0 || loc.y < 0 || loc.x >= grid.width || loc.y >= grid.height
        fun isTileMissing(it: Location) = state.ruleState[it] ?: grid[it.x, it.y].state == Missing

        return locationsTouching(state.block).none { isOffGrid(it) || isTileMissing(it) }
    }

    private fun locationsTouching(block: Block): List<Location> =
        when (Pair(block.orientation, block.height)) {
            Pair(X, 2) -> listOf(block.location, Location(block.location.x + 1, block.location.y))
            Pair(Y, 2) -> listOf(block.location, Location(block.location.x, block.location.y + 1))
            else -> listOf(block.location)
        }

    fun isAtSink(v: State, grid: Grid) = v.block.location == grid.sinkLocation() && v.block.orientation == Z


}
