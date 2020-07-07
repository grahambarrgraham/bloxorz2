package search

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class GraphSearchTest {

    data class Vertex(val id: String)
    data class Edge(val v1: Vertex, val v2: Vertex, val cost: Int)

    @Test fun shortestPathHappyCase() {

        val vertices = List(6) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 7),
            Edge(vertices[0], vertices[5], 14),
            Edge(vertices[0], vertices[2], 9),
            Edge(vertices[1], vertices[2], 10),
            Edge(vertices[1], vertices[3], 15),
            Edge(vertices[2], vertices[5], 2),
            Edge(vertices[2], vertices[3], 11),
            Edge(vertices[3], vertices[4], 6),
            Edge(vertices[4], vertices[5], 9)
        )

        testShortestPath(edges, vertices[0], vertices[4], 20, 3)
    }

    @Test fun shortestPathSourceAndSinkTheSame() {

        val vertices = List(6) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 7),
            Edge(vertices[0], vertices[5], 14),
            Edge(vertices[0], vertices[2], 9),
            Edge(vertices[1], vertices[2], 10),
            Edge(vertices[1], vertices[3], 15),
            Edge(vertices[2], vertices[5], 2),
            Edge(vertices[2], vertices[3], 11),
            Edge(vertices[3], vertices[4], 6),
            Edge(vertices[4], vertices[5], 9)
        )

        testShortestPath(edges, vertices[1], vertices[1], 0, 0)
    }


    @Test(expected = GraphSearch.NoPathFound::class) fun shortestPathNoPathToSink() {

        val vertices = List(7) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 7),
            Edge(vertices[0], vertices[5], 14),
            Edge(vertices[0], vertices[2], 9),
            Edge(vertices[1], vertices[2], 10),
            Edge(vertices[1], vertices[3], 15),
            Edge(vertices[2], vertices[5], 2),
            Edge(vertices[2], vertices[3], 11),
            Edge(vertices[3], vertices[4], 6),
            Edge(vertices[4], vertices[5], 9)
        )

        testShortestPath(edges, vertices[1], vertices[6], 0, 0)
    }

    @Test fun shortestPathsChoosePrefersLeastEdges() {

        val vertices = List(6) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 3),
            Edge(vertices[0], vertices[2], 1),
            Edge(vertices[1], vertices[2], 2)
        )

        testShortestPath(edges, vertices[1], vertices[0], 3, 1)
    }

    @Test fun allPathsHappyCase() {

        val vertices = List(6) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 7),
            Edge(vertices[0], vertices[5], 14),
            Edge(vertices[0], vertices[2], 9),
            Edge(vertices[1], vertices[2], 10),
            Edge(vertices[1], vertices[3], 15),
            Edge(vertices[2], vertices[5], 2),
            Edge(vertices[2], vertices[3], 11),
            Edge(vertices[3], vertices[4], 6),
            Edge(vertices[4], vertices[5], 9)
        )

        testAllPaths(edges, vertices[0], vertices[4], listOf(
            ExpectedPath(20, 3),
            ExpectedPath(23, 2),
            ExpectedPath(26, 3),
            ExpectedPath(28, 3),
            ExpectedPath(34, 4)
        ))
    }

    @Test fun allPathsNoRoute() {

        val vertices = List(7) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 7),
            Edge(vertices[0], vertices[5], 14),
            Edge(vertices[0], vertices[2], 9),
            Edge(vertices[1], vertices[2], 10),
            Edge(vertices[1], vertices[3], 15),
            Edge(vertices[2], vertices[5], 2),
            Edge(vertices[2], vertices[3], 11),
            Edge(vertices[3], vertices[4], 6),
            Edge(vertices[4], vertices[5], 9)
        )

        testAllPaths(edges, vertices[0], vertices[6], listOf())
    }

    @Test fun allPathsPreferLeastEdges() {

        val vertices = List(4) { Vertex((it + 1).toString()) }

        val edges = listOf(
            Edge(vertices[0], vertices[1], 1),
            Edge(vertices[0], vertices[2], 2),
            Edge(vertices[0], vertices[3], 3),
            Edge(vertices[1], vertices[2], 1),
            Edge(vertices[1], vertices[3], 1),
            Edge(vertices[2], vertices[3], 1)
        )

        testAllPaths(edges, vertices[0], vertices[3], listOf(
            ExpectedPath(2, 2),
            ExpectedPath(3, 1),
            ExpectedPath(3, 2),
            ExpectedPath(3, 3)
        ))
    }


    data class ExpectedPath(val cost: Int, val edges: Int)

    private fun testAllPaths(
        edges: List<Edge>,
        start: Vertex,
        finish: Vertex,
        expectedPaths: List<ExpectedPath>
    ) {
        val result = allPaths(edges, start, finish)

        result.forEach {
            println("Cost ${it.cost}")
            println("Edges ${it.history.map { edge -> edge.destination }}")
        }

        val actualPaths = result.map {
            ExpectedPath(
                it.cost,
                it.history.size
            )
        }

        assertThat(actualPaths, `is`(expectedPaths))
    }

    private fun testShortestPath(
        edges: List<Edge>,
        start: Vertex,
        finish: Vertex,
        expectedCost: Int,
        expectedEdges: Int
    ) {
        val result = shortestPath(edges, start, finish)
        println("Cost ${result.cost}")
        println("Edges ${result.history.map { it.destination }}")
        assertThat(result.cost, equalTo(expectedCost))
        assertThat(result.history.size, equalTo(expectedEdges))
    }


    private fun shortestPath(edges: List<Edge>, start: Vertex, finish: Vertex) : GraphSearch.Path<Vertex, String> {

        val duplexEdges = edges.plus(edges.map {
            Edge(
                it.v2,
                it.v1,
                it.cost
            )
        })

        fun generateActions(vertex: Vertex) = duplexEdges.filter { it.v1 == vertex }
            .map { GraphSearch.Edge(it.cost, it.v2, "any action") }

        return GraphSearch.shortestPath(start, { v -> v == finish }, { v -> generateActions(v) })
    }

    private fun allPaths(edges: List<Edge>, start: Vertex, finish: Vertex) : List<GraphSearch.Path<Vertex, String>> {

        val duplexEdges = edges.plus(edges.map {
            Edge(
                it.v2,
                it.v1,
                it.cost
            )
        })

        fun generateActions(vertex: Vertex) = duplexEdges.filter { it.v1 == vertex }
            .map { GraphSearch.Edge(it.cost, it.v2, "any action") }

        return GraphSearch.allPaths(start, { v -> v == finish }, { v -> generateActions(v) }).toList()
    }

}
