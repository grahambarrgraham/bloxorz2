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
    fun orientationYIsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Y, 2, true)
    }

    @Test
    fun orientationXIsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, X, 2, true)
    }

    @Test
    fun orientationZIsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Z, 2, true)
    }

    @Test
    fun orientationZOnTopRightBoundaryIsLegal() {
        checkIsLegal("/allnormal.txt", 4, 4, Z, 2, true)
    }

    @Test
    fun overBoundaryInOrientatinoXIsIllegal() {
        checkIsLegal("/allnormal.txt", 4, 4, X, 2, false)
    }

    @Test
    fun overBoundaryInOrientationYIsIllegal() {
        checkIsLegal("/allnormal.txt", 4, 4, Y, 2, false)
    }

    @Test
    fun touchingOneMissingTileInOrientationXIsIllegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 2, false)
    }

    @Test
    fun touchingOneMissingTileOrientationIsIllegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 2, false)
    }

    @Test
    fun touchingMissingTileInOrientationZIsIllegal() {
        checkIsLegal("/mostlymissing.txt", 3, 3, Y, 2, false)
    }

    @Test
    fun invalidLocationIsIllegal() {
        checkIsLegal("/allnormal.txt", -1, 0, Y, 2, false)
    }

    @Test
    fun height1TileInOrientationXIsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 1, true)
    }

    @Test
    fun height1TileInOrientationYIsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 1, true)
    }

    @Test
    fun touchingWeakTileInOrientationXIsLegal() {
        checkIsLegal("/weak.txt", 0, 1, X, 2, true)
    }

    @Test
    fun touchingWeakTileInOrientationYIsLegal() {
        checkIsLegal("/weak.txt", 1, 0, Y, 2, true)
    }

    @Test
    fun touchingWeakTileInOrientationZIsIllegal() {
        checkIsLegal("/weak.txt", 1, 1, Z, 2, false)
    }

    @Test
    fun touchingWeakTileInOrientationZWithHeight1IsLegal() {
        checkIsLegal("/weak.txt", 1, 1, Z, 1, true)
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
    fun strongOpens() {
        checkRule(Location(1, 5), X, Action.Left, Location(3, 5), Present)
        checkRule(Location(1, 5), X, Action.Left, Location(4, 5), Present)
    }

    @Test
    fun strongCloses() {
        checkRule(Location(1, 4), X, Action.Left, Location(3, 4), Missing)
        checkRule(Location(1, 4), X, Action.Left, Location(4, 4), Missing)

    }

    @Test
    fun strongToggle() {
        checkRule(Location(1, 3), X, Action.Left, Location(3, 3), Present)
        checkRule(Location(1, 3), X, Action.Left, Location(4, 3), Missing)
    }

    @Test
    fun strongOpenNoActionIfOrientationIsNotZ() {
        checkRule(Location(0, 6), X, Action.Down, Location(3, 5), Missing)
        checkRule(Location(0, 6), X, Action.Down, Location(4, 5), Present)
        checkRule(Location(1, 5), Y, Action.Left, Location(3, 5), Missing)
        checkRule(Location(1, 5), Y, Action.Left, Location(4, 5), Present)
    }

    @Test
    fun strongOpenNotActivatedIfOrientationZWithHeight1() {
        checkRule(Location(1, 5), X, Action.Left, Location(3, 5), Missing, 1)
        checkRule(Location(1, 5), X, Action.Left, Location(4, 5), Present, 1)
    }


    @Test
    fun strongCloseNoActionIfOrientationIsNotZ() {
        checkRule(Location(0, 5), X, Action.Down, Location(3, 4), Missing)
        checkRule(Location(0, 5), X, Action.Down, Location(4, 4), Present)
        checkRule(Location(1, 4), Y, Action.Left, Location(3, 4), Missing)
        checkRule(Location(1, 4), Y, Action.Left, Location(4, 4), Present)
    }

    @Test
    fun strongCloseNotActivatedIfOrientationZWithHeight1() {
        checkRule(Location(1, 4), X, Action.Left, Location(3, 4), Missing, 1)
        checkRule(Location(1, 4), X, Action.Left, Location(4, 4), Present, 1)
    }


    @Test
    fun strongToggleNoActionIfOrientationIsNotZ() {
        checkRule(Location(0, 4), X, Action.Down, Location(3, 3), Missing)
        checkRule(Location(0, 4), X, Action.Down, Location(4, 3), Present)
        checkRule(Location(1, 3), Y, Action.Left, Location(3, 3), Missing)
        checkRule(Location(1, 3), Y, Action.Left, Location(4, 3), Present)
    }

    @Test
    fun strongToggleNotActivatedIfOrientationZWithHeight1() {
        checkRule(Location(1, 3), X, Action.Left, Location(3, 3), Missing, 1)
        checkRule(Location(1, 3), X, Action.Left, Location(4, 3), Present, 1)
    }

    @Test
    fun weakOpens() {
        checkRule(Location(1, 2), X, Action.Left, Location(3, 2), Present)
        checkRule(Location(1, 2), X, Action.Left, Location(4, 2), Present)
    }

    @Test
    fun weakCloses() {
        checkRule(Location(1, 1), X, Action.Left, Location(3, 1), Missing)
        checkRule(Location(1, 1), X, Action.Left, Location(4, 1), Missing)
    }

    @Test
    fun weakToggle() {
        checkRule(Location(1, 0), X, Action.Left, Location(3, 0), Present)
        checkRule(Location(1, 0), X, Action.Left, Location(4, 0), Missing)
    }

    @Test
    fun ruleAffectsMultipleTilesWithSameTag() {
        //tile changed
        checkRule(Location(1, 6), X, Action.Left, Location(3, 6), Missing)
        checkRule(Location(1, 6), X, Action.Left, Location(4, 6), Missing)

        //tile not changed
        checkRule(Location(1, 6), X, Action.Left, Location(3, 7), Missing)
        checkRule(Location(1, 6), X, Action.Left, Location(4, 7), Missing)

        //tile changed
        checkRule(Location(1, 7), X, Action.Left, Location(3, 6), Present)
        checkRule(Location(1, 7), X, Action.Left, Location(4, 6), Present)

        //tile not changed
        checkRule(Location(1, 7), X, Action.Left, Location(3, 7), Present)
        checkRule(Location(1, 7), X, Action.Left, Location(4, 7), Present)
    }

    @Test
    fun twoSwitchesControlSameTile() {
        checkRule(Location(1, 8), X, Action.Left, Location(3, 7), Present)
        checkRule(Location(1, 8), X, Action.Left, Location(4, 7), Present)

        checkRule(Location(1, 8), X, Action.Left, Location(3, 6), Missing)
        checkRule(Location(1, 8), X, Action.Left, Location(4, 6), Missing)
    }

    private fun checkRule(
        startLoc: Location,
        startOrientation: Orientation,
        action: Action,
        targetLocation: Location,
        expectedState: BloxorzGrid.TileState,
        height: Int = 2
    ) {
        //TODO refactor as fluent
        val grid = BloxorzGrid.load("/rules.txt")
        var state = State(Block(startLoc, startOrientation, height), Start, grid.initialRuleState())
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