package bloxorz

import bloxorz.BloxorzSearch.allPaths
import bloxorz.BloxorzSearch.condensedFormat
import bloxorz.BloxorzSearch.detailedFormat
import bloxorz.BloxorzSearch.expandToActionList
import bloxorz.BloxorzSearch.playActionList
import bloxorz.BloxorzSearch.shortestPath
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class BloxorzSearchTest {

    @Test
    fun condensePath() {
        val path = shortestPath("/level1.txt")
        val condensed = condensedFormat(path)
        assertThat(condensed, `is`("[R, D2, R2, D, R]"))
    }

    @Test
    fun detailedPath() {
        val path = shortestPath("/level1.txt")
        val s = detailedFormat(path)
        assertThat(s, `is`("[R->(2,4)X, D->(2,3)X, D->(2,2)X, R->(4,2)Z, R->(5,2)X, D->(5,1)X, R->(7,1)Z]"))
    }

    @Test
    fun level1ShortestPath() {
        val path = shortestPath("/level1.txt")
        println(condensedFormat(path))
        assertThat(path.cost, `is`(7))
    }

    @Test
    fun level1AllPaths() {
        val paths = allPaths("/level1.txt").toList()
        paths.map {detailedFormat(it)}.forEach { println(it) }
        assertThat(paths.first().cost, `is`(7))
    }

    @Test
    fun playActions() {
        val grid = BloxorzGrid.load("/level1.txt")
        assertThat(playActionList(grid, expandToActionList("[R, D2, R2, D, R]")), `is`(true))
    }

    @Test
    fun playActionsOffGrid() {
        val grid = BloxorzGrid.load("/level1.txt")
        assertThat(playActionList(grid, expandToActionList("[L10]")), `is`(false))
    }

    @Test
    fun playActionsNotToSink() {
        val grid = BloxorzGrid.load("/level1.txt")
        assertThat(playActionList(grid, expandToActionList("[R, D2, R2, D]")), `is`(false))
    }
}