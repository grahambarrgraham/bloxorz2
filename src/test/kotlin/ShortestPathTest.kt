package org.rrabarg

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ShortestPathTest {

    data class Vertex(val id: String)
    data class Edge(val v1: Vertex, val v2: Vertex, val cost: Int)

    @Test fun graphSearchHappyCase() {

        val vertices = List(6) { Vertex((it+1).toString()) }

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

        testSearch(edges, vertices[0], vertices[4], 20, 3)
    }

    @Test fun graphSearchSourceAndSinkTheSame() {

        val vertices = List(6) { Vertex((it+1).toString()) }

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

        testSearch(edges, vertices[1], vertices[1], 0, 0)
    }


    @Test(expected = ShortestPath.NoPathFound::class) fun noPathFromSourceToSink() {

        val vertices = List(7) { Vertex((it+1).toString()) }

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

        testSearch(edges, vertices[1], vertices[6], 0, 0)
    }

    private fun testSearch(
        edges: List<Edge>,
        start: Vertex,
        finish: Vertex,
        expectedCost: Int,
        expectedEdges: Int
    ) {
        val result = search(edges, start, finish)
        println("Cost ${result.cost}")
        println("Edges ${result.history.map { it -> it.destination }}")
        assertThat(result.cost, equalTo(expectedCost))
        assertThat(result.history.size, equalTo(expectedEdges))
    }

    fun search(edges: List<Edge>, start: Vertex, finish: Vertex) : ShortestPath.Path<Vertex> {

        val duplexEdges = edges.plus(edges.map { Edge(it.v2, it.v1, it.cost) })

        fun generateActions(vertex: Vertex) = duplexEdges.filter { it.v1 == vertex }
            .map { ShortestPath.Edge(it.cost, it.v2) }

        return ShortestPath.search(start, { v -> v == finish }, { v -> generateActions(v) })
    }
}
