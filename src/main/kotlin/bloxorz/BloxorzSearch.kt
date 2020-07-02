package bloxorz

import bloxorz.BloxorzGame.Orientation.Z
import search.GraphSearch

object BloxorzSearch {

    fun shortestPath(filename: String): GraphSearch.Path<BloxorzGame.State> {

        val grid = BloxorzGrid.load(filename)
        val initialState = BloxorzGame.State(BloxorzGame.Block(grid.sourceLocation(), Z, 2))

        return GraphSearch.shortestPath(initialState,
            { v -> v.block.location == grid.sinkLocation() && v.block.orientation == Z },
            { v -> BloxorzGame.generateMoves(grid, v) }
        )

        // implement path rewrite
    }

}
