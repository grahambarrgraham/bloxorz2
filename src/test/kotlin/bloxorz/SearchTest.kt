package bloxorz

import bloxorz.Formatting.condensedFormat
import bloxorz.Search.shortestPathForward
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SearchTest {

    @Test
    fun level1ShortestPath() {
        val path = shortestPathForward(Grid.load("/level1.txt")).path
        println(condensedFormat(path))
        assertThat(path.cost, `is`(7))
    }

}