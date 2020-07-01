package org.rrabarg

import java.lang.Exception
import java.util.PriorityQueue

fun main() {
}

class ShortestPath {

    data class Action<T>(val cost: Int, val destination: T)
    data class State<T>(val cost: Int = 0, val history: List<Action<T>> = emptyList())
    data class Node<T>(val location: T, val state: State<T>)

    fun <T> search(from: T,
               isFinal: (T) -> Boolean,
               generateActions: (T) -> List<Action<T>>) : State<T> {

        fun nextState(state: State<T>, it: Action<T>) =
            State(state.cost + it.cost, state.history + it)

        val queue = PriorityQueue<Node<T>>(compareBy { it.state.cost })
        val visited = mutableMapOf<T, State<T>>()

        queue.add(Node(from, State()))
        visited[from] = State()

        while (true) {

            val node = queue.poll() ?: throw Exception("No Path Found")

            if (isFinal(node.location)) {
                return node.state
            }

            generateActions(node.location).forEach {
                val nextState = nextState(node.state, it)
                val cheapest= visited[it.destination]?.cost ?: Int.MAX_VALUE

                if (nextState.cost < cheapest) {
                    queue.add(Node(it.destination, nextState))
                    visited[node.location] = node.state
                }
            }
        }
    }
}
