package bloxorz

import bloxorz.Formatting.condensedFormat
import bloxorz.Formatting.detailedFormat
import bloxorz.Formatting.expandToActionList
import bloxorz.Search.IllegalAction
import bloxorz.Search.shortestPathForward
import junit.framework.TestCase.fail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class FormattingTest {

    @Test
    fun condensePath() {
        assertThat(condensedFormat(shortestPathForward(Grid.load("/level1.txt")).path), `is`("[R, D2, R2, D, R]"))
    }

    @Test
    fun detailedPath() {
        val s = detailedFormat(shortestPathForward(Grid.load("/level1.txt")).path)
        assertThat(s, `is`("[R->(2,4)X/2, D->(2,3)X/2, D->(2,2)X/2, R->(4,2)Z/2, R->(5,2)X/2, D->(5,1)X/2, R->(7,1)Z/2]"))
    }

}