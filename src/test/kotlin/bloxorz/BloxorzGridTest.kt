package bloxorz

import bloxorz.BloxorzGame.Rule
import bloxorz.BloxorzGame.Rule.*
import bloxorz.BloxorzGame.Rule.Type.WeakToggle
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileState.*
import bloxorz.BloxorzGrid.load
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGridTest {

    @Test
    fun graphWithoutRulesLoads() {

        val load = load("/level1.txt")

        println(load)

        assertThat(load[0,0].state, `is`(Missing))
        assertThat(load[16,0].state, `is`(Missing))
        assertThat(load[16,5].state, `is`(Missing))
        assertThat(load[0,5].state, `is`(Present))
    }

    @Test
    fun graphWithRulesLoads() {
        val load = load("/rules.txt")
        println(load)
        assertThat(load.rules.size, `is`(24))

        //strong switches
        assertThat(load.rules[0], `is`(Rule(Type.StrongOpen, Location(0, 5), Location(3, 5))))
        assertThat(load.rules[1], `is`(Rule(Type.StrongOpen, Location(0, 5), Location(4, 5))))
        assertThat(load.rules[2], `is`(Rule(Type.StrongClose, Location(0, 4), Location(3, 4))))
        assertThat(load.rules[3], `is`(Rule(Type.StrongClose, Location(0, 4), Location(4, 4))))
        assertThat(load.rules[4], `is`(Rule(Type.StrongToggle, Location(0, 3), Location(3, 3))))
        assertThat(load.rules[5], `is`(Rule(Type.StrongToggle, Location(0, 3), Location(4, 3))))

        //weak switches
        assertThat(load.rules[6], `is`(Rule(Type.WeakOpen, Location(0, 2), Location(3, 2))))
        assertThat(load.rules[7], `is`(Rule(Type.WeakOpen, Location(0, 2), Location(4, 2))))
        assertThat(load.rules[8], `is`(Rule(Type.WeakClose, Location(0, 1), Location(3, 1))))
        assertThat(load.rules[9], `is`(Rule(Type.WeakClose, Location(0, 1), Location(4, 1))))
        assertThat(load.rules[10], `is`(Rule(Type.WeakToggle, Location(0, 0), Location(3, 0))))
        assertThat(load.rules[11], `is`(Rule(Type.WeakToggle, Location(0, 0), Location(4, 0))))

        //switch affecting multiple tiles with the same tag
        assertThat(load.rules[12], `is`(Rule(Type.StrongToggle, Location(0, 6), Location(3, 6))))
        assertThat(load.rules[13], `is`(Rule(Type.StrongToggle, Location(0, 6), Location(4, 6))))
        assertThat(load.rules[14], `is`(Rule(Type.StrongToggle, Location(0, 7), Location(3, 7))))
        assertThat(load.rules[15], `is`(Rule(Type.StrongToggle, Location(0, 7), Location(4, 7))))

        //switch affecting tiles which are also controlled by other switches
        assertThat(load.rules[16], `is`(Rule(Type.StrongToggle, Location(0, 8), Location(3, 6))))
        assertThat(load.rules[17], `is`(Rule(Type.StrongToggle, Location(0, 8), Location(4, 6))))
        assertThat(load.rules[18], `is`(Rule(Type.StrongToggle, Location(0, 8), Location(3, 7))))
        assertThat(load.rules[19], `is`(Rule(Type.StrongToggle, Location(0, 8), Location(4, 7))))

        //switch affecting multiple tiles, declared in one rule, with comma delimited tags
        assertThat(load.rules[20], `is`(Rule(Type.StrongToggle, Location(0, 9), Location(3, 6))))
        assertThat(load.rules[21], `is`(Rule(Type.StrongToggle, Location(0, 9), Location(4, 6))))
        assertThat(load.rules[22], `is`(Rule(Type.StrongToggle, Location(0, 9), Location(3, 7))))
        assertThat(load.rules[23], `is`(Rule(Type.StrongToggle, Location(0, 9), Location(4, 7))))

    }

    @Test
    fun tilesWithRuleTagsHaveCorrectState() {
        val load = load("/rules.txt")

        assertThat(load[3,5].state, `is`(Missing))
        assertThat(load[3,4].state, `is`(Missing))
        assertThat(load[3,3].state, `is`(Missing))
        assertThat(load[3,2].state, `is`(Missing))
        assertThat(load[3,1].state, `is`(Missing))
        assertThat(load[3,0].state, `is`(Missing))

        assertThat(load[4,5].state, `is`(Present))
        assertThat(load[4,4].state, `is`(Present))
        assertThat(load[4,3].state, `is`(Present))
        assertThat(load[4,2].state, `is`(Present))
        assertThat(load[4,1].state, `is`(Present))
        assertThat(load[4,0].state, `is`(Present))

    }

    @Test
    fun initialRuleStateIsSet() {
        val load = load("/rules.txt")

        val expectedInitialState = mapOf(
            Pair(Location(3, 5), Missing),
            Pair(Location(4, 5), Present),
            Pair(Location(3, 4), Missing),
            Pair(Location(4, 4), Present),
            Pair(Location(3, 3), Missing),
            Pair(Location(4, 3), Present),
            Pair(Location(3, 2), Missing),
            Pair(Location(4, 2), Present),
            Pair(Location(3, 1), Missing),
            Pair(Location(4, 1), Present),
            Pair(Location(3, 0), Missing),
            Pair(Location(4, 0), Present),
            Pair(Location(3, 6), Present),
            Pair(Location(4, 6), Present),
            Pair(Location(3, 7), Missing),
            Pair(Location(4, 7), Missing)

        )
        assertThat(load.initialRuleState(), equalTo(expectedInitialState))
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