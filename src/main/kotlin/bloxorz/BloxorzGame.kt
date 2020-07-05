package bloxorz

import bloxorz.BloxorzGame.Action.*
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGame.Rule.Type.*
import bloxorz.BloxorzGrid.Grid
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState
import bloxorz.BloxorzGrid.TileState.Missing
import bloxorz.BloxorzGrid.TileState.Present
import search.GraphSearch
import java.lang.Math.abs


object BloxorzGame {

    enum class Orientation {
        X, Y, Z
    }

    enum class Action(val code: Char) {
        Up('U'), Down('D'), Left('L'), Right('R'), Start('S'), SwitchBlock('B')
    }

    data class Block(val location: Location, val orientation: Orientation, val height: Int)

    data class State(
        val activeBlock: Block,
        val ruleState: Map<Location, TileState>,
        val inactiveBlock: Block? = null
    ) {
        //TODO make this part of Path.
        //this is a hack to hitch this information, but it isn't actually part part of the Vertex state.  Including
        //this as part of the state leadds to poorer pruning in the search tree, as the same vertex is navigated
        //multiple times, once for action will resulted in reaching it
        var action: Action = Start

        constructor(
            activeBlock: Block,
            ruleState: Map<Location, TileState>,
            inactiveBlock: Block? = null,
            action: Action
        ) : this(activeBlock, ruleState, inactiveBlock) {
            this.action = action
        }
    }

    data class Rule(
        val type: Type,
        val subjectLocation: Location,
        val objectLocation: Location,
        val secondObjectLocation: Location? = null
    ) {
        enum class Type {
            WeakToggle, StrongToggle, WeakClose, StrongClose, WeakOpen, StrongOpen, Teleport
        }
    }

    fun initialState(grid: Grid) = State(Block(grid.sourceLocation(), Z, 2), grid.initialRuleState(), action = Start)

    fun generateMoves(grid: Grid, v: State): List<GraphSearch.Edge<State>> {

        return Action.values()
            .filterNot { it == Start }
            .filterNot { it == SwitchBlock && v.inactiveBlock == null }
            .map { generateNextState(grid, it, v) }
            .filter { isLegal(grid, it) }
            .map { GraphSearch.Edge(1, it) }
    }

    fun generateNextState(grid: Grid, action: Action, currentState: State): State {

        val x = currentState.activeBlock.location.x
        val y = currentState.activeBlock.location.y
        val blockHeight = currentState.activeBlock.height
        val blockWidth = 1
        val orientation = currentState.activeBlock.orientation

        //SwitchBlock, swap active and inactive block
        if (action == SwitchBlock) {
            if (currentState.inactiveBlock == null) {
                return currentState
            } else
                return State(
                    currentState.inactiveBlock,
                    currentState.ruleState,
                    currentState.activeBlock,
                    currentState.action
                )
        }

        //move block, create next block
        var nextBlock = when (Pair(action, orientation)) {
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

        //teleport splits blocks
        if (nextBlock.orientation == Z) {
            val rules = grid.rulesAt(nextBlock.location)
            if (rules.size == 1) {
                val rule = rules[0]
                if (rule.type == Teleport) {
                    val activeBlock = Block(rule.objectLocation, Z, 1)
                    val block2 = Block(rule.secondObjectLocation!!, Z, 1)
                    return State(activeBlock, currentState.ruleState, block2, action)
                }
            }
        }

        //var join blocks in to single block if they are adjacent
        var inactiveBlock = currentState.inactiveBlock
        if (inactiveBlock != null && adjacent(nextBlock.location, inactiveBlock.location)) {
            nextBlock = join(nextBlock, inactiveBlock)
            inactiveBlock = null
        }

        val newRuleState = applyRules(grid, currentState.ruleState, nextBlock)

        return State(nextBlock, newRuleState, inactiveBlock = inactiveBlock, action = action)
    }

    private fun join(a: Block, b: Block): Block {
        return if (a.location.x < b.location.x) Block(a.location, X, 2)
        else if (b.location.x < a.location.x) Block(b.location, X, 2)
        else if (a.location.y < b.location.y) Block(a.location, Y, 2)
        else if (b.location.y < a.location.y) Block(b.location, Y, 2)
        else throw Exception("Can't join $a and $b, they are the same")
    }

    private fun adjacent(a: Location, b: Location): Boolean = abs(a.x - b.x) + abs(a.y - b.y) == 1

    private fun applyRules(grid: Grid, currentRuleState: Map<Location, TileState>, block: Block)
            : MutableMap<Location, TileState> {

        fun applyRule(rule: Rule?, currentTileState: TileState): TileState {
            return when (rule?.type) {
                WeakToggle -> currentTileState.opposite()
                WeakClose -> Missing
                StrongClose -> if (block.orientation == Z && block.height > 1) Missing else currentTileState
                StrongToggle -> if (block.orientation == Z && block.height > 1) currentTileState.opposite() else currentTileState
                StrongOpen -> if (block.orientation == Z && block.height > 1) Present else currentTileState
                WeakOpen -> Present
                else -> currentTileState
            }
        }

        val applicableRules = locationsTouching(block).flatMap { grid.rulesAt(it) }
        val newRuleState = mutableMapOf<Location, TileState>()

        currentRuleState.forEach {
            val ruleAtLocation = applicableRules.find { rule -> rule.objectLocation == it.key }
            newRuleState[it.key] = applyRule(ruleAtLocation, it.value)
        }

        return newRuleState
    }

    fun isLegal(grid: Grid, state: State): Boolean {

        fun isOffGrid(loc: Location) = loc.x < 0 || loc.y < 0 || loc.x >= grid.width || loc.y >= grid.height
        fun isTileMissing(it: Location) = state.ruleState[it] ?: grid[it.x, it.y].state == Missing
        fun isWeakTileBroken(it: Location) =
            grid[it.x, it.y].weak && state.activeBlock.orientation == Z && state.activeBlock.height > 1

        return locationsTouching(state.activeBlock).none { isOffGrid(it) || isTileMissing(it) || isWeakTileBroken(it) }
    }

    private fun locationsTouching(block: Block): List<Location> =
        when (Pair(block.orientation, block.height)) {
            Pair(X, 2) -> listOf(block.location, Location(block.location.x + 1, block.location.y))
            Pair(Y, 2) -> listOf(block.location, Location(block.location.x, block.location.y + 1))
            else -> listOf(block.location)
        }

    fun isAtSink(state: State, grid: Grid) =
        state.activeBlock.location == grid.sinkLocation()
                && state.activeBlock.orientation == Z
                && state.activeBlock.height > 1
                && state.inactiveBlock == null

}
