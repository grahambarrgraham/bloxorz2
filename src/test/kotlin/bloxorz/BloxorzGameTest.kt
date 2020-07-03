package bloxorz

import bloxorz.BloxorzGame.Action.Start
import bloxorz.BloxorzGame.Block
import bloxorz.BloxorzGame.Orientation
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGame.State
import bloxorz.BloxorzGame.isLegal
import bloxorz.BloxorzGrid.Location
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGameTest {

    @Test
    fun happyY() {
        checkIsLegal("/mostlymissing.txt",2, 2, Y, 2, true)
    }

    @Test
    fun happyX() {
        checkIsLegal("/mostlymissing.txt",2, 2, X, 2, true)
    }

    @Test
    fun happyZ() {
        checkIsLegal("/mostlymissing.txt",2, 2, Z, 2, true)
    }

    @Test
    fun boundaryHappy() {
        checkIsLegal("/allnormal.txt",4, 4, Z, 2, true)
    }

    @Test
    fun overBoundaryX() {
        checkIsLegal("/allnormal.txt",4, 4, X, 2, false)
    }

    @Test
    fun overBoundaryY() {
        checkIsLegal("/allnormal.txt",4, 4, Y, 2, false)
    }

    @Test
    fun touchingOneMissingX() {
        checkIsLegal("/mostlymissing.txt",2, 3, X, 2, false)
    }

    @Test
    fun touchingOneMissingY() {
        checkIsLegal("/mostlymissing.txt",2, 3, Y, 2, false)
    }

    @Test
    fun touchingOneMissingZ() {
        checkIsLegal("/mostlymissing.txt",3, 3, Y, 2, false)
    }

    @Test
    fun invalidLocation() {
        checkIsLegal("/allnormal.txt",-1, 0, Y, 2, false)
    }

    @Test
    fun height1IsOkayX() {
        checkIsLegal("/mostlymissing.txt",2, 3, X, 1, true)
    }

    @Test
    fun height1IsOkayY() {
        checkIsLegal("/mostlymissing.txt",2, 3, Y, 1, true)
    }

    private fun checkIsLegal(
        gridName : String,
        x: Int,
        y: Int,
        orientation: Orientation,
        height: Int,
        expected: Boolean
    ) {
        val grid = BloxorzGrid.load(gridName)
        val legal = isLegal(grid, State(Block(Location(x, y), orientation, height), Start, grid.initialRuleState()))
        assertThat(legal, `is`(expected))
    }

}