package org.rrabarg

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ShortestPathTest {
    @Test fun graphSearchHappyCase() {
        val result = GraphSearch().search()
        println("Distance ${result.cost}")
        println("Distance ${result.history.map { it -> it.destination }}")
        assertThat(result.cost, equalTo(20))
        assertThat(result.history.size, equalTo(3))
    }
}

class GraphSearch {

    data class Vertex(val id: String)

    data class Edge(val v1: Vertex, val v2: Vertex, val cost: Int)

    private val vertices = List(6) { Vertex((it+1).toString()) }

    private var edges = listOf(
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

    fun search() : ShortestPath.Path<Vertex> {
        val start = vertices[0]
        val finish = vertices[4]

        edges = edges.plus(edges.map { Edge(it.v2, it.v1, it.cost) })

        fun generateActions(vertex: Vertex) = edges.filter { it.v1 == vertex }
            .map { ShortestPath.Edge(it.cost, it.v2) }

        return ShortestPath()
            .search(start, { v -> v == finish }, { v -> generateActions(v) })
    }
}
