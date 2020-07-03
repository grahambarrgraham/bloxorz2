package bloxorz

import bloxorz.BloxorzGame.Rule
import bloxorz.BloxorzGrid.Location
import bloxorz.BloxorzGrid.TileType.*
import bloxorz.BloxorzGrid.load
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class BloxorzGridTest {

    @Test
    fun graphLoads() {

        val load = load("/level1.txt")

        println(load)

        assertThat(load[0,0].type, `is`(Missing))
        assertThat(load[16,0].type, `is`(Missing))
        assertThat(load[16,5].type, `is`(Missing))
        assertThat(load[0,5].type, `is`(Normal))
        assertThat(load[1,4].type, `is`(Source))
        assertThat(load[7,1].type, `is`(Sink))
    }

    @Test
    fun graphWithRulesLoads() {
        val load = load("/withtoggles.txt")
        println(load)
        assertThat(load.rules.size, `is`(4))
        assertThat(load.rules[0], `is`(Rule(Rule.Type.WeakToggle, Location(0, 0), Location(3, 2))))
        assertThat(load.rules[1], `is`(Rule(Rule.Type.StrongToggle, Location(4, 0), Location(2, 1))))
        assertThat(load.rules[2], `is`(Rule(Rule.Type.WeakClose, Location(4, 4), Location(2, 3))))
        assertThat(load.rules[3], `is`(Rule(Rule.Type.StrongClose, Location(0, 4), Location(0, 1))))

    }

    @Test
    fun soureLocation() {
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