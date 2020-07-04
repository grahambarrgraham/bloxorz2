package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.Start
import bloxorz.BloxorzGame.Block
import bloxorz.BloxorzGame.Orientation
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGame.State
import bloxorz.BloxorzGame.isLegal
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState.Missing
import bloxorz.BloxorzGrid.TileState.Present
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGameTest {

    @Test
    fun happyY() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Y, 2, true)
    }

    @Test
    fun happyX() {
        checkIsLegal("/mostlymissing.txt", 2, 2, X, 2, true)
    }

    @Test
    fun happyZ() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Z, 2, true)
    }

    @Test
    fun boundaryHappy() {
        checkIsLegal("/allnormal.txt", 4, 4, Z, 2, true)
    }

    @Test
    fun overBoundaryX() {
        checkIsLegal("/allnormal.txt", 4, 4, X, 2, false)
    }

    @Test
    fun overBoundaryY() {
        checkIsLegal("/allnormal.txt", 4, 4, Y, 2, false)
    }

    @Test
    fun touchingOneMissingX() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 2, false)
    }

    @Test
    fun touchingOneMissingY() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 2, false)
    }

    @Test
    fun touchingOneMissingZ() {
        checkIsLegal("/mostlymissing.txt", 3, 3, Y, 2, false)
    }

    @Test
    fun invalidLocation() {
        checkIsLegal("/allnormal.txt", -1, 0, Y, 2, false)
    }

    @Test
    fun height1IsOkayX() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 1, true)
    }

    @Test
    fun height1IsOkayY() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 1, true)
    }

    @Test
    fun initialStateOfTaggedTilesWithRulesIsCorrect() {
        //x1, x2, x3, x4
        checkIsLegal("/withToggles.txt", 1, 0, Z, 2, false)
        checkIsLegal("/withToggles.txt", 2, 1, Z, 2, false)
        checkIsLegal("/withToggles.txt", 3, 1, Z, 2, false)
        checkIsLegal("/withToggles.txt", 3, 1, Z, 3, false)

        //p1, p3, p4
        checkIsLegal("/withToggles.txt", 3, 2, Z, 2, true)
        checkIsLegal("/withToggles.txt", 3, 2, Z, 2, true)
        checkIsLegal("/withToggles.txt", 0, 1, Z, 2, true)
    }

    @Test
    fun weakToggle() {
        checkRule(Location(1, 0), Y, Action.Left, Location(2, 1), Missing)
        checkRule(Location(0, 1), X, Action.Down, Location(2, 1), Missing)
    }

    @Test
    fun strongToggleNoActionIfOrientationIsNotZ() {
        checkRule(Location(3, 0), Y, Action.Right, Location(2, 1), Missing)
        checkRule(Location(3, 0), Y, Action.Right, Location(3, 1), Missing)
        checkRule(Location(3, 1), X, Action.Down, Location(2, 1), Missing)
        checkRule(Location(3, 1), X, Action.Down, Location(3, 1), Missing)
    }

    @Test
    fun strongToggle() {
        checkRule(Location(4, 1), Y, Action.Down, Location(2, 1), Present)
    }

    @Test
    fun ruleCanAffectMultipleTiles() {
        checkRule(Location(4, 1), Y, Action.Down, Location(3, 1), Present)
        checkRule(Location(4, 1), Y, Action.Down, Location(2, 1), Present)
    }

    @Test
    fun strongCloses() {
        checkRule(Location(0, 2), Y, Action.Up, Location(0, 1), Missing)
    }

    @Test
    fun weakCloses() {
        checkRule(Location(4, 2), Z, Action.Up, Location(2, 3), Missing)
    }

    @Test
    fun strongOpens() {
        checkRule(Location(1, 1), Y, Action.Up, Location(3, 3), Present)
    }

    @Test
    fun weakOpens() {
        checkRule(Location(1, 0), Z, Action.Up, Location(1, 1), Present)
    }

    @Test
    fun strongCloseNoActionIfOrientationIsNotZ() {
        checkRule(Location(1, 3), Y, Action.Left, Location(0, 1), Present)
        checkRule(Location(0, 3), X, Action.Up, Location(0, 1), Present)
    }

    @Test
    fun strongOpenNoActionIfOrientationIsNotZ() {
        checkRule(Location(0, 2), Y, Action.Right, Location(3, 3), Missing)
        checkRule(Location(0, 2), X, Action.Up, Location(3, 3), Missing)
    }

    private fun checkRule(
        startLoc: Location,
        startOrientation: Orientation,
        action: Action,
        targetLocation: Location,
        expectedState: BloxorzGrid.TileState
    ) {
        //TODO refactor as fluent
        val grid = BloxorzGrid.load("/withToggles.txt")
        var state = State(Block(startLoc, startOrientation, 2), Start, grid.initialRuleState())
        state = BloxorzGame.generateNextState(grid, action, state)
        assertThat(state.ruleState[targetLocation], `is`(expectedState))
    }

    private fun checkIsLegal(
        gridName: String,
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