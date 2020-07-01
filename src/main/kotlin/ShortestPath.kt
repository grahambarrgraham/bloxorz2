package org.rrabarg

import java.lang.Exception
import java.util.PriorityQueue

/**
 * Finds the lowest cost path from a source Node, to a sink Node, in a graph of connected Nodes. Each edge
 * in the graph has an associated cost of traversal, and so the cost of the path from source to sink is defined as
 * the sum of the edge costs' that were traversed on that path.
 */
object ShortestPath {

    //An Edge on the graph relative to a source Vertex.
    data class Edge<V>(val cost: Int, val destination: V)

    //A costed Path through the graph, with an ordered list of each Edge traversed
    data class Path<V>(val cost: Int = 0, val history: List<Edge<V>> = emptyList())

    class NoPathFound : Exception("No path found")

    fun <Vertex> search(
        source: Vertex,
        isSink: (Vertex) -> Boolean,
        edges: (Vertex) -> List<Edge<Vertex>>
    ): Path<Vertex> {

        val queue = PriorityQueue<Path<Vertex>>(compareBy { it.cost })
        val visited = mutableMapOf<Vertex, Path<Vertex>>()

        fun currentVertex(path: Path<Vertex>) = path.history.lastOrNull()?.destination ?: source

        queue.add(Path())
        visited[source] = Path()

        while (true) {

            val path = queue.poll() ?: throw NoPathFound()
            val vertex = currentVertex(path)
            if (isSink(vertex)) {
                return path
            }

            edges(vertex).forEach {
                val nextPath = Path(path.cost + it.cost, path.history + it)
                val cheapest = visited[it.destination]?.cost ?: Int.MAX_VALUE

                if (nextPath.cost < cheapest) {
                    queue.add(nextPath)
                    visited[vertex] = path
                }
            }
        }
    }
}
