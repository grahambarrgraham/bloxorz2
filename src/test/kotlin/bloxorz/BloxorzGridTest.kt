package bloxorz

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