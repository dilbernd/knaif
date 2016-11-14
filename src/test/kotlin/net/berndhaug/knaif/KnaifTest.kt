package net.berndhaug.knaif

import org.junit.Assert.assertEquals
import org.junit.Test

class KnaifTest {
    @Test
    fun numbersFromDurationLine() {
        assertEquals(listOf(listOf(2, 25), listOf(160)), Duration.timeValsFromDurationLine("    2:25-160 "))
        assertEquals(listOf(listOf(1200), listOf(1, 20, 15)), Duration.timeValsFromDurationLine("    1200 -1:20:15 "))
        assertEquals(listOf(listOf(10, 4), listOf(1, 22, 30)), Duration.timeValsFromDurationLine("10:04 - 1:22:30"))
    }


    @Test
    fun durationsAsSeconds() {
        assertEquals(listOf(145, 160), Duration.secondsFromTimeVals(listOf(listOf(2, 25), listOf(160))))
        assertEquals(listOf(1200, 4815), Duration.secondsFromTimeVals(listOf(listOf(1200), listOf(1, 20, 15))))
        assertEquals(listOf(604, 4950), Duration.secondsFromTimeVals(listOf(listOf(10, 4), listOf(1, 22, 30))))
    }

    @Test
    fun fullDurations() {
        assertEquals(Duration(145, 160), Duration.fromLine("    2:25-160 "))
        assertEquals(Duration(1200, 4815), Duration.fromLine("    1200 -1:20:15 "))
        assertEquals(Duration(604, 4950), Duration.fromLine("10:04 - 1:22:30"))
    }

    @Test
    fun fullInputToDurations() {

        assertEquals(listOf(Duration(145, 160),
                Duration(1200, 4815),
                Duration(604, 4950)), durationsFromCuttingDirectionsContent("""    2:25-160
1200 -1:20:15
10:04 - 1:22:30
"""))
    }

    @Test
    fun testInverter() {
        assertEquals(listOf(Duration(0, 145), Duration(161, 1200), Duration(3126, 4128), Duration(4951, Int.MAX_VALUE)),
                invertDurations(listOf(Duration(145, 160), Duration(1200, 3125), Duration(4128, 4950))))
    }

    @Test
    fun testCommands() {
        val expected = """ffconcat version 1.0
file testing.mp4

outpoint 145
file testing.mp4
inpoint 161
outpoint 1200
file testing.mp4
inpoint 3126
outpoint 4128
file testing.mp4
inpoint 4951
"""
        val commands = makeCommands("testing.mp4",
                listOf(Duration(0, 145), Duration(161, 1200), Duration(3126, 4128), Duration(4951, Int.MAX_VALUE)))
        assertEquals(expected, commands)
    }
}
