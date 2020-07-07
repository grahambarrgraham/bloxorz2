package bloxorz

import bloxorz.BloxorzGame.Action
import bloxorz.BloxorzGame.Action.SwitchBlock
import bloxorz.BloxorzGame.Block
import bloxorz.BloxorzGame.Orientation
import bloxorz.BloxorzGame.Orientation.*
import bloxorz.BloxorzGame.State
import bloxorz.BloxorzGame.isAtSink
import bloxorz.BloxorzGame.isLegal
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState.Missing
import bloxorz.BloxorzGrid.TileState.Present
import junit.framework.Assert.assertNull
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGameTest {

    @Test
    fun orientationY_IsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Y, 2, true)
    }

    @Test
    fun orientationX_IsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, X, 2, true)
    }

    @Test
    fun orientationZ_IsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 2, Z, 2, true)
    }

    @Test
    fun orientationZOnTopRightBoundary_IsLegal() {
        checkIsLegal("/allnormal.txt", 4, 4, Z, 2, true)
    }

    @Test
    fun overBoundaryInOrientatinoX_IsIllegal() {
        checkIsLegal("/allnormal.txt", 4, 4, X, 2, false)
    }

    @Test
    fun overBoundaryInOrientationY_IsIllegal() {
        checkIsLegal("/allnormal.txt", 4, 4, Y, 2, false)
    }

    @Test
    fun touchingOneMissingTileInOrientationX_IsIllegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 2, false)
    }

    @Test
    fun touchingOneMissingTileOrientation_IsIllegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 2, false)
    }

    @Test
    fun touchingMissingTileInOrientationZ_IsIllegal() {
        checkIsLegal("/mostlymissing.txt", 3, 3, Y, 2, false)
    }

    @Test
    fun invalidLocation_IsIllegal() {
        checkIsLegal("/allnormal.txt", -1, 0, Y, 2, false)
    }

    @Test
    fun height1TileInOrientationX_IsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, X, 1, true)
    }

    @Test
    fun height1TileInOrientationY_IsLegal() {
        checkIsLegal("/mostlymissing.txt", 2, 3, Y, 1, true)
    }

    @Test
    fun touchingWeakTileInOrientationX_IsLegal() {
        checkIsLegal("/weak.txt", 0, 1, X, 2, true)
    }

    @Test
    fun touchingWeakTileInOrientationY_IsLegal() {
        checkIsLegal("/weak.txt", 1, 0, Y, 2, true)
    }

    @Test
    fun touchingWeakTileInOrientationZ_IsIllegal() {
        checkIsLegal("/weak.txt", 1, 1, Z, 2, false)
    }

    @Test
    fun touchingWeakTileInOrientationZWithHeight1_IsLegal() {
        checkIsLegal("/weak.txt", 1, 1, Z, 1, true)
    }

    @Test
    fun touchingWeakSwitchInOrientationZ_IsLegal() {
        checkIsLegal("/rules.txt", 0, 0, Z, 2, true)
    }

    @Test
    fun touchingWeakSwitchInOrientationX_IsLegal() {
        checkIsLegal("/rules.txt", 0, 0, X, 2, true)
    }

    @Test
    fun touchingWeakSwitchInOrientationY_IsLegal() {
        checkIsLegal("/rules.txt", 0, 0, Y, 2, true)
    }

    @Test
    fun touchingTeleportInOrientationZ_IsLegal() {
        checkIsLegal("/rules.txt", 0, 10, Z, 2, true)
    }

    @Test
    fun touchingTeleportInOrientationX_IsLegal() {
        checkIsLegal("/rules.txt", 0, 10, X, 2, true)
    }

    @Test
    fun touchingTeleportInOrientationY_IsLegal() {
        checkIsLegal("/rules.txt", 0, 10, Y, 2, true)
    }

    @Test
    fun initialStateOfTaggedMissingTiles_WithRules_IsCorrect() {
        //x1, x2, x3, x4
        checkIsLegal("/withToggles.txt", 1, 0, Z, 2, false)
        checkIsLegal("/withToggles.txt", 2, 1, Z, 2, false)
        checkIsLegal("/withToggles.txt", 3, 1, Z, 2, false)
        checkIsLegal("/withToggles.txt", 3, 1, Z, 3, false)
    }

    @Test
    fun initialStateOfTaggedPresentTiles_WithRules_IsCorrect() {
        //p1, p3, p4
        checkIsLegal("/withToggles.txt", 3, 2, Z, 2, true)
        checkIsLegal("/withToggles.txt", 3, 2, Z, 2, true)
        checkIsLegal("/withToggles.txt", 0, 1, Z, 2, true)
    }

    @Test
    fun strongOpen_RulesApplied() {
        checkRule(Location(1, 5), X, Action.Left, Location(3, 5), Present)
        checkRule(Location(1, 5), X, Action.Left, Location(4, 5), Present)
    }

    @Test
    fun strongClose_RulesApplied() {
        checkRule(Location(1, 4), X, Action.Left, Location(3, 4), Missing)
        checkRule(Location(1, 4), X, Action.Left, Location(4, 4), Missing)

    }

    @Test
    fun strongToggle_OrientationZ_RulesApplied() {
        checkRule(Location(1, 3), X, Action.Left, Location(3, 3), Present)
        checkRule(Location(1, 3), X, Action.Left, Location(4, 3), Missing)
    }

    @Test
    fun strongOpen_OrientationIsNotZ_NotApplied() {
        checkRule(Location(0, 6), X, Action.Down, Location(3, 5), Missing)
        checkRule(Location(0, 6), X, Action.Down, Location(4, 5), Present)
        checkRule(Location(1, 5), Y, Action.Left, Location(3, 5), Missing)
        checkRule(Location(1, 5), Y, Action.Left, Location(4, 5), Present)
    }

    @Test
    fun strongOpen_BlockHeightIs1_NotApplied() {
        checkRule(Location(1, 5), X, Action.Left, Location(3, 5), Missing, 1)
        checkRule(Location(1, 5), X, Action.Left, Location(4, 5), Present, 1)
    }


    @Test
    fun strongClose_OrientationNotZ_NotApplied() {
        checkRule(Location(0, 5), X, Action.Down, Location(3, 4), Missing)
        checkRule(Location(0, 5), X, Action.Down, Location(4, 4), Present)
        checkRule(Location(1, 4), Y, Action.Left, Location(3, 4), Missing)
        checkRule(Location(1, 4), Y, Action.Left, Location(4, 4), Present)
    }

    @Test
    fun strongClose_BlockIsHeight1_NotApplied() {
        checkRule(Location(1, 4), X, Action.Left, Location(3, 4), Missing, 1)
        checkRule(Location(1, 4), X, Action.Left, Location(4, 4), Present, 1)
    }


    @Test
    fun strongToggle_OrientationNotZ_NotApplied() {
        checkRule(Location(0, 4), X, Action.Down, Location(3, 3), Missing)
        checkRule(Location(0, 4), X, Action.Down, Location(4, 3), Present)
        checkRule(Location(1, 3), Y, Action.Left, Location(3, 3), Missing)
        checkRule(Location(1, 3), Y, Action.Left, Location(4, 3), Present)
    }

    @Test
    fun strongToggle_BlockHeightIs1_NotApplied() {
        checkRule(Location(1, 3), X, Action.Left, Location(3, 3), Missing, 1)
        checkRule(Location(1, 3), X, Action.Left, Location(4, 3), Present, 1)
    }

    @Test
    fun weakOpens_RuleApplied() {
        checkRule(Location(1, 2), X, Action.Left, Location(3, 2), Present)
        checkRule(Location(1, 2), X, Action.Left, Location(4, 2), Present)
    }

    @Test
    fun weakCloses_RuleApplied() {
        checkRule(Location(1, 1), X, Action.Left, Location(3, 1), Missing)
        checkRule(Location(1, 1), X, Action.Left, Location(4, 1), Missing)
    }

    @Test
    fun weakToggle_RuleApplied() {
        checkRule(Location(1, 0), X, Action.Left, Location(3, 0), Present)
        checkRule(Location(1, 0), X, Action.Left, Location(4, 0), Missing)
    }

    @Test
    fun multipleTilesWithSameTag_RuleAppliedToAll() {

        //toggled from present to missing
        checkRule(Location(1, 6), X, Action.Left, Location(3, 6), Missing)
        checkRule(Location(1, 6), X, Action.Left, Location(4, 6), Missing)

        //toggled from missing to present
        checkRule(Location(1, 7), X, Action.Left, Location(3, 7), Present)
        checkRule(Location(1, 7), X, Action.Left, Location(4, 7), Present)

    }

    @Test
    fun multipleTagged_DifferentTags_TagsWithSame2ndChar_RuleNotApplied() {

        //tiles unchanged
        checkRule(Location(1, 6), X, Action.Left, Location(3, 7), Missing)
        checkRule(Location(1, 6), X, Action.Left, Location(4, 7), Missing)

        //tiles unchanged
        checkRule(Location(1, 7), X, Action.Left, Location(3, 6), Present)
        checkRule(Location(1, 7), X, Action.Left, Location(4, 6), Present)

    }

    @Test
    fun twoSwitchesControlSameTile_RuleApplied() {
        checkRule(Location(1, 8), X, Action.Left, Location(3, 7), Present)
        checkRule(Location(1, 8), X, Action.Left, Location(4, 7), Present)

        checkRule(Location(1, 8), X, Action.Left, Location(3, 6), Missing)
        checkRule(Location(1, 8), X, Action.Left, Location(4, 6), Missing)
    }

    @Test
    fun teleport_OrientationZ_RuleApplied() {
        checkTeleport(Location(0,8), Y, Action.Up, Location(0, 10), Location(4, 0))
    }

    @Test
    fun teleport_OrientationX_RuleNotApplied() {
        checkTeleport(Location(0,9), X, Action.Up, Location(0, 10), firstBlockOrientation = X, firstBlockHeight = 2)
    }

    @Test
    fun teleport_OrientationY_RuleNotApplied() {
        checkTeleport(Location(1,9), Y, Action.Left, Location(0, 9), firstBlockOrientation = Y, firstBlockHeight = 2)
        checkTeleport(Location(1,10), Y, Action.Left, Location(0, 10), firstBlockOrientation = Y, firstBlockHeight = 2)
    }

    @Test
    fun sink_OrientationZ_Height2_IsSink() {
        checkIsSink(Block(Location(5, 10), Z, 2), true)
    }

    @Test
    fun sink_OrientationZ_Height1_IsNotSink() {
        checkIsSink(Block(Location(5, 10), Z, 1), false)
    }

    @Test
    fun sink_OrientationNotZ_Height2_IsNotSink() {
        checkIsSink(Block(Location(4, 10), X, 2), false)
        checkIsSink(Block(Location(5, 9), Y, 2), false)
    }

    @Test
    fun switchBlock_ActiveAndInactiveBlock_Swap() {
        val grid = BloxorzGrid.load("/rules.txt")
        val block1 = Block(Location(0, 0), Z, 1)
        val block2 = Block(Location(1, 1), Z, 1)
        val state = State(block1, grid.initialRuleState(), block2)
        val newState = BloxorzGame.generateNextState(grid, SwitchBlock, state)
        assertThat(newState.ruleState, equalTo(state.ruleState))
        assertThat(newState.activeBlock, equalTo(block2))
        assertThat(newState.inactiveBlock, equalTo(block1))
    }

    @Test
    fun switchBlock_SinglBlock_NoOp() {
        val grid = BloxorzGrid.load("/rules.txt")
        val block1 = Block(Location(0, 0), Z, 2)
        val state = State(block1, grid.initialRuleState())
        val newState = BloxorzGame.generateNextState(grid, SwitchBlock, state)
        assertThat(newState, equalTo(state))
    }

    @Test
    fun splitBlocks_OnlyActiveBlock_Moves() {
        val grid = BloxorzGrid.load("/rules.txt")
        val block1 = Block(Location(0, 0), Z, 1)
        val block2 = Block(Location(2, 2), Z, 1)
        val state = State(block1, grid.initialRuleState(), block2)
        val newState = BloxorzGame.generateNextState(grid, Action.Right, state)
        assertThat(newState.ruleState, equalTo(state.ruleState))
        assertThat(newState.activeBlock, equalTo(Block(Location(1, 0), Z, 1)))
        assertThat(newState.inactiveBlock, equalTo(block2))
    }

    @Test
    fun splitBlocks_NextToEachOther_OrientationY_Rejoin() {
        val grid = BloxorzGrid.load("/rules.txt")
        val block1 = Block(Location(0, 0), Z, 1)
        val block2 = Block(Location(1, 1), Z, 1)
        val state = State(block1, grid.initialRuleState(), block2)
        val newState = BloxorzGame.generateNextState(grid, Action.Right, state)
        assertThat(newState.ruleState, equalTo(state.ruleState))
        assertThat(newState.activeBlock, equalTo(Block(Location(1, 0), Y, 2)))
        assertNull(newState.inactiveBlock)
    }

    @Test
    fun splitBlocks_NextToEachOther_OrientationX_Rejoin() {
        val grid = BloxorzGrid.load("/rules.txt")
        val block1 = Block(Location(2, 2), Z, 1)
        val block2 = Block(Location(1, 1), Z, 1)
        val state = State(block1, grid.initialRuleState(), block2)
        val newState = BloxorzGame.generateNextState(grid, Action.Down, state)
        assertThat(newState.ruleState, equalTo(state.ruleState))
        assertThat(newState.activeBlock, equalTo(Block(Location(1, 1), X, 2)))
        assertNull(newState.inactiveBlock)
    }


    private fun checkIsSink(activeBlock: Block, isSink: Boolean, secondBlock: Block ?= null) {
        val grid = BloxorzGrid.load("/rules.txt")
        val state = State(activeBlock, grid.initialRuleState(), secondBlock)
        assertThat(isAtSink(state, grid), `is`(isSink))
    }

    private fun checkTeleport(
        startLoc: Location,
        startOrientation: Orientation,
        action: Action,
        firstTargetLocation: Location,
        secondTargetLocation: Location ?= null,
        firstBlockOrientation: Orientation = Z,
        firstBlockHeight: Int = 1
    ) {
        //TODO refactor to be fluent fluent
        val grid = BloxorzGrid.load("/rules.txt")
        val state = State(Block(startLoc, startOrientation, 2), grid.initialRuleState())
        val newState = BloxorzGame.generateNextState(grid, action, state)
        assertThat(newState.ruleState, equalTo(state.ruleState))
        assertThat(newState.activeBlock, equalTo(Block(firstTargetLocation, firstBlockOrientation, firstBlockHeight)))
        if (secondTargetLocation == null) {
            assertNull(newState.inactiveBlock)
        } else {
            assertThat(newState.inactiveBlock, equalTo(Block(secondTargetLocation, Z, 1)))
        }

    }


    private fun checkRule(
        startLoc: Location,
        startOrientation: Orientation,
        action: Action,
        targetLocation: Location,
        expectedState: BloxorzGrid.TileState,
        height: Int = 2
    ) {
        //TODO refactor to be fluent fluent
        val grid = BloxorzGrid.load("/rules.txt")
        var state = State(Block(startLoc, startOrientation, height), grid.initialRuleState())
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
        val legal = isLegal(grid, State(
            Block(Location(x, y), orientation, height),
            grid.initialRuleState()
        ))
        assertThat(legal, `is`(expected))
    }

}