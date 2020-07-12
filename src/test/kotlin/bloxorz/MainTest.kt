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

class MainTest {

    @Test
    fun playback_ToSink() {
        val grid = Grid.load("/level1.txt")
        val state = Main.playback(grid, expandToActionList("[R, D2, R2, D, R]"))
        assertThat(Game.isAtSink(grid, state), `is`(true))
    }

    @Test(expected = IllegalAction::class)
    fun playback_OffGrid() {
        val grid = Grid.load("/level1.txt")
        Main.playback(grid, expandToActionList("[L10]"))
        fail()
    }

    @Test
    fun playback_NotAtSink() {
        val grid = Grid.load("/level1.txt")
        val state = Main.playback(grid, expandToActionList("[R, D2, R2, D]"))
        assertThat(Game.isAtSink(grid, state), `is`(false))
    }
}