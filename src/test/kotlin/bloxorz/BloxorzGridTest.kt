package bloxorz

import bloxorz.BloxorzGame.Rule
import bloxorz.BloxorzGame.Rule.*
import bloxorz.BloxorzGame.Rule.Type.WeakToggle
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState.*
import bloxorz.BloxorzGrid.load
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGridTest {

    @Test
    fun graphLoads() {

        val load = load("/level1.txt")

        println(load)

        assertThat(load[0,0].state, `is`(Missing))
        assertThat(load[16,0].state, `is`(Missing))
        assertThat(load[16,5].state, `is`(Missing))
        assertThat(load[0,5].state, `is`(Present))
        }

    @Test
    fun graphWithRulesLoads() {
        val load = load("/withtoggles.txt")
        println(load)
        assertThat(load.rules.size, `is`(7))
        assertThat(load.rules[0], `is`(Rule(WeakToggle, Location(0, 0), Location(3, 2))))
        assertThat(load.rules[1], `is`(Rule(Type.StrongToggle, Location(4, 0), Location(2, 1))))
        assertThat(load.rules[2], `is`(Rule(Type.StrongToggle, Location(4, 0), Location(3, 1))))
        assertThat(load.rules[3], `is`(Rule(Type.WeakClose, Location(4, 4), Location(2, 3))))
        assertThat(load.rules[4], `is`(Rule(Type.StrongClose, Location(0, 4), Location(0, 1))))
        assertThat(load.rules[5], `is`(Rule(Type.StrongOpen, Location(1, 3), Location(3, 3))))
        assertThat(load.rules[6], `is`(Rule(Type.WeakOpen, Location(1, 2), Location(1, 1))))
    }

    @Test
    fun tilesWithTagsHaveCorrectType() {
        val load = load("/withtoggles.txt")
        assertThat(load[3,2].state, `is`(Present))
        assertThat(load[2,1].state, `is`(Missing))
        assertThat(load[4,4].state, `is`(Present))
        assertThat(load[0,4].state, `is`(Present))
        assertThat(load[1,0].state, `is`(Missing))
        assertThat(load[2,3].state, `is`(Present))
    }

    @Test
    fun rulesInitialState() {
        val load = load("/withtoggles.txt")

        val expectedInitialState = mapOf(
            Pair(Location(3, 2), Present),
            Pair(Location(2, 1), Missing),
            Pair(Location(3, 1), Missing),
            Pair(Location(2, 3), Present),
            Pair(Location(0, 1), Present),
            Pair(Location(1, 1), Missing),
            Pair(Location(3, 3), Missing)


        )
        assertThat(load.initialRuleState(), `is`(expectedInitialState))
    }

    @Test
    fun sourceLocation() {
        val load = load("/level1.txt")
        println(load)
        assertThat(load.sourceLocation(), `is`(Location(1, 4)))
    }

    @Test
    fun sinkLocation() {
        val load = load("/level1.txt")
        println(load)
        assertThat(load.sinkLocation(), `is`(Location(7, 1)))
    }

}