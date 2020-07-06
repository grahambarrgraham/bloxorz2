package bloxorz

import bloxorz.BloxorzSearch.IllegalAction
import bloxorz.BloxorzSearch.allPaths
import bloxorz.BloxorzSearch.condensedFormat
import bloxorz.BloxorzSearch.detailedFormat
import bloxorz.BloxorzSearch.expandToActionList
import bloxorz.BloxorzSearch.playActionList
import bloxorz.BloxorzSearch.shortestPath
import junit.framework.Assert.fail
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
        assertThat(s, `is`("[R->(2,4)X/2, D->(2,3)X/2, D->(2,2)X/2, R->(4,2)Z/2, R->(5,2)X/2, D->(5,1)X/2, R->(7,1)Z/2]"))
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
    fun level2AllPaths() {
        val paths = allPaths("/level2.txt").toList()
        paths.map { condensedFormat(it) }.forEach { println(it) }
        assertThat(paths.first().cost, `is`(17))
    }

    @Test
    fun level3AllPaths() {
        val paths = allPaths("/level3.txt").toList()
        paths.map { condensedFormat(it) }.forEach { println(it) }
        assertThat(paths.first().cost, `is`(19))
    }

    @Test
    fun level4AllPaths() {
        val paths = allPaths("/level4.txt").toList()
        paths.map { condensedFormat(it) }.forEach { println(it) }
        assertThat(paths.first().cost, `is`(28))
    }

    @Test
    fun playActions() {
        val grid = BloxorzGrid.load("/level1.txt")
        assertThat(playActionList(grid, expandToActionList("[R, D2, R2, D, R]")), `is`(true))
    }

    @Test(expected = IllegalAction::class)
    fun playActionsOffGrid() {
        val grid = BloxorzGrid.load("/level1.txt")
        playActionList(grid, expandToActionList("[L10]"))
        fail()
    }

    @Test
    fun playActionsNotToSink() {
        val grid = BloxorzGrid.load("/level1.txt")
        assertThat(playActionList(grid, expandToActionList("[R, D2, R2, D]")), `is`(false))
    }
}