package bloxorz

import bloxorz.BloxorzSearch.condensePath
import bloxorz.BloxorzSearch.shortestPath
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class BloxorzSearchTest {

    @Test
    fun condensePath() {
        val path = shortestPath("/level1.txt")
        val condensed = condensePath(path)
        assertThat(condensed, `is`("[R, D2, R2, D, R]"))
    }

    @Test
    fun level1ShortestPath() {
        val path = shortestPath("/level1.txt")
        println(condensePath(path))
        assertThat(path.cost, `is`(7))
    }
}