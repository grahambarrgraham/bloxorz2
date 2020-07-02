package search

import java.lang.Exception
import java.util.PriorityQueue

object GraphSearch {

    //An Edge on the graph relative to a source Vertex.
    data class Edge<V>(val cost: Int, val destination: V)

    //A costed Path through the graph, with an ordered list of each Edge traversed
    data class Path<V>(val cost: Int = 0, val history: List<Edge<V>> = emptyList())

    class NoPathFound : Exception("No path found")

    /**
     * Finds the lowest cost path from a source Node, to a sink Node, in a graph of connected Nodes. Each edge
     * in the graph has an associated cost of traversal, and so the cost of the path from source to sink is defined as
     * the sum of the edge costs' that were traversed on that path.
     * If two paths have the same cost, then the one with the least edges traversed is deemed the lowest cost.
     */
    fun <Vertex> shortestPath(
        source: Vertex,
        isSink: (Vertex) -> Boolean,
        edges: (Vertex) -> List<Edge<Vertex>>
    ): Path<Vertex> = allPaths(
        source,
        isSink,
        edges
    ).firstOrNull() ?: throw NoPathFound()


    /**
     * Finds the all of the paths from a source Node, to a sink Node, in a graph of connected Nodes. Each edge
     * in the graph has an associated cost of traversal, and so the cost of the path from source to sink is defined as
     * the sum of the edge costs' that were traversed on that path. The results are sorted in increasing order of cost.
     * If two paths have the same cost, then the one with the least edges traversed is deemed the lowest cost.
     */
    fun <Vertex> allPaths(
        source: Vertex,
        isSink: (Vertex) -> Boolean,
        edges: (Vertex) -> List<Edge<Vertex>>
    ): Sequence<Path<Vertex>> {

        val queue = PriorityQueue<Path<Vertex>>(compareBy<Path<Vertex>> { it.cost }.thenBy { it.history.size })
        val visited = mutableMapOf<Vertex, Path<Vertex>>()

        fun currentVertex(path: Path<Vertex>) = path.history.lastOrNull()?.destination ?: source

        queue.add(Path())
        visited[source] = Path()

        return sequence {
            while (true) {

                val path = queue.poll() ?: break
                val vertex = currentVertex(path)
                if (isSink(vertex)) {
                    yield(path)
                }

                edges(vertex).forEach {
                    val nextPath =
                        Path(path.cost + it.cost, path.history + it)
                    val cheapest = visited[it.destination]?.cost ?: Int.MAX_VALUE

                    if (nextPath.cost < cheapest) {
                        queue.add(nextPath)
                        visited[vertex] = path
                    }
                }
            }
        }
    }
}
