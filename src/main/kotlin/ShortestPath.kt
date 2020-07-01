package org.rrabarg

import java.lang.Exception
import java.util.PriorityQueue

/**
 * Finds the lowest cost path from a source Node, to a sink Node, in a graph of connected Nodes. Each edge
 * in the graph has an associated cost of traversal, and so the cost of the path from source to sink is defined as
 * the sum of the edge costs' that were traversed on that path.
 */
class ShortestPath {

    //An Edge on the graph relative to a source Vertex.
    data class Edge<V>(val cost: Int, val destination: V)

    //A costed path through the graph, with an ordered list of each Edge traversed
    data class Path<V>(val cost: Int = 0, val history: List<Edge<V>> = emptyList())

    data class VertexState<Vertex>(val vertex: Vertex, val path: Path<Vertex>)

    fun <Vertex> search(source: Vertex,
                        isSink: (Vertex) -> Boolean,
                        enumerateEdges: (Vertex) -> List<Edge<Vertex>>)
            : Path<Vertex> {

        val queue = PriorityQueue<VertexState<Vertex>>(compareBy { it.path.cost })
        val visited = mutableMapOf<Vertex, Path<Vertex>>()

        queue.add(VertexState(source, Path()))
        visited[source] = Path()

        while (true) {

            val vertexState = queue.poll() ?: throw Exception("No Path Found")

            if (isSink(vertexState.vertex)) {
                return vertexState.path
            }

            enumerateEdges(vertexState.vertex).forEach {
                val nextPath = Path(vertexState.path.cost + it.cost, vertexState.path.history + it)
                val cheapest= visited[it.destination]?.cost ?: Int.MAX_VALUE

                if (nextPath.cost < cheapest) {
                    queue.add(VertexState(it.destination, nextPath))
                    visited[vertexState.vertex] = vertexState.path
                }
            }
        }
    }
}
