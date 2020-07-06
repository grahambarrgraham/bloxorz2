package bloxorz

import bloxorz.BloxorzSearch.condensedFormat

fun main(args: Array<String>) {

    //level 15 61/63 best : 57 ([R4, U2, S, U5, R3, U2, R4, D2, S, L4, S, U, D, U, L, U, L3, D, S, L5, D, L2, D3, L, D, R, U, R7])
    //level 16 32/33
    //level 23 no result 75
    //level 26 no result / 106
    //level 28 no result / 101

    playActionsOnLevel("/level8.txt",
        "[R2, D, S, U4, D, R2]"
    )

    playActionsOnLevel("/level21.txt", "[R, D, L, U, L, D, R, U, R2, U, R3, U, L, D3, U3, R, D, L3, D, L2, D, L, U, R, D, R, U, L, D, R, U, L, D, R, D2, R, D2, R3, U2, D2, L2, U, L, D, R3, U4, R3]")

    playActionsOnLevel("/level21.txt",
        "[R, D, L, U, L, D, R, U, R2, U, R3, U, L, D3, U3, R, D, L3, D, L2, D, L, U, R, D, R, U, L, D, R, U, L, D, R, D2, R, D2, R3, U2, D2, L2, U, L, D, R3, U4, R3]"
    )

     playActionsOnLevel("/level15.txt",
        "[R4, U2, S, U5, R3, U2, R4, D2, S, L4, S, U, D, U, L, U, L3, D, S, L5, D, L2, D3, L, D, R, U, R7]"
    )

    playActionsOnLevel("/level16.txt",
        "[R4, S, R, D, S, R, U, S, R, L3, R4, L5, D, S, R, U, S, R4]"
    )

    playActionsOnLevel("/level23.txt",
        "[R, D2, R, U, R5, U, L, D, R, L, U, L2, U4, R3, U, R, D, L4, D4, L9, U3, R2, U, D, L, U, R, D2, R, D3, R2, D, R, U6, R, D, L, U]"
    )

    playActionsOnLevel("/level26.txt",
        "[U2, L3, D, L3, D, R, U, R2, U2, R2, D, R2, D, R2, U2, L2, U, L3, D, L5, D, L, D2, S, U3, L4, D, L4, D2, L, D, U2, R, U, R3, U, R3, D, R2, U2, L2, U, L3, D, L, D3, R2, D, R, S, U3, L3, D, L, D3, R2, D, L]"
    )

    playActionsOnLevel("/level28.txt",
        "[L, D3, R, D, L, U, R, D2, R2, D2, R3, U3, L, U, L, U, L, U, L, D, R2, D, R, D, R, D, R2, D, L, D, L2, U3, L, U, L, U, L2, U, L2, D, L, U, R3, D, R, D, R, D, R, D, R, L4, D3, L4, U, L, U, L3, D, S, L6, U, L, U, L3, U]"
    )

}

private fun playActionsOnLevel(level: String, actions: String) {
    val grid = BloxorzGrid.load(level)
    var completed = false
    var count = 0
    try {
        val actionList = BloxorzSearch.expandToActionList(actions)
        count = actionList.size
        completed = BloxorzSearch.playActionList(grid, actionList)
    } catch (e: Exception) {
        println(e.message)
    }
    println("Playback of $actions in $level was $completed ($count moves)")
}

object Scratch

private fun reformatBloxorz1Log() {
    val parts = Scratch.javaClass.getResource("/bloxorz-1-log.txt").readText().lines()

    (1..33).asSequence().forEach {
        val i = (4 * it) - 2
        val line = parts[i]
        val match = Regex("(\\d+) - \\[(.+)\\]$").find(line)!!
        val (length, b1Moves) = match.destructured
        val condensed = bloxorz1FormatCondensed(b1Moves)
        val match2 = Regex("Completed in (\\d+) ms with (\\d+) expansions").find(parts[i+1])!!
        val (duration, expansions) = match2.destructured
        println("level $it : $length moves : $condensed : took $duration ms with $expansions expansions")
    }
}


private fun bloxorz1FormatCondensed(str: String) : String {
    val map = str.split("1, ").map {
        val find = "Move (\\w+)->".toRegex().find(it)
        val (instr: String) = find!!.destructured
        instr
    }.map {
        when (it) {
            "up" -> 'U'
            "down" -> 'D'
            "left" -> 'L'
            "right" -> 'R'
            "nextBlock" -> 'S'
            else -> throw Exception("Unknown instruction $it")
        }
    }

    return condensedFormat(map)
}

