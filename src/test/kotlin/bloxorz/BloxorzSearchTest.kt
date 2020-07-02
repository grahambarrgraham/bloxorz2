package bloxorz

import bloxorz.BloxorzSearch.shortestPath
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class BloxorzSearchTest {

    @Test
    fun level1() {
        val path = shortestPath("/level1.txt")
        println(path)
        assertThat(path.cost, `is`(7))
    }
}