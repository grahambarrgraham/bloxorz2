package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.Start
import bloxorz.BloxorzGame.Orientation.Z
import search.GraphSearch
import java.util.*

object BloxorzSearch {

    fun shortestPath(filename: String): GraphSearch.Path<BloxorzGame.State> {

        val grid = BloxorzGrid.load(filename)
        val initialState = BloxorzGame.State(BloxorzGame.Block(grid.sourceLocation(), Z, 2), Start)

        return GraphSearch.shortestPath(initialState,
            { v -> v.block.location == grid.sinkLocation() && v.block.orientation == Z },
            { v -> BloxorzGame.generateMoves(grid, v) }
        )
    }

    fun condensePath(path: GraphSearch.Path<BloxorzGame.State>): String {

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

}
