package bloxorz

import bloxorz.BloxorzSearch.condensedFormat

fun main(args: Array<String>) {
    //val str = "Move up->node:[(1,2)@(2,6)-y]-1, Move left->node:[(1,2)@(1,6)-y]-11, Move down->node:[(1,2)@(1,5)-z]-11, Move right->node:[(1,2)@(2,5)-x]-111, Move right->node:[(1,2)@(4,5)-z]-111, Move right->node:[(1,2)@(5,5)-x]-111, Move right->node:[(1,2)@(7,5)-z]-111, Move up->node:[(1,2)@(7,6)-y]-111, Move up->node:[(1,2)@(7,8)-z]-111, Move down->node:[(1,2)@(7,6)-y]-1111, Move down->node:[(1,2)@(7,5)-z]-1111, Move left->node:[(1,2)@(5,5)-x]-1111, Move left->node:[(1,2)@(4,5)-z]-1111, Move left->node:[(1,2)@(2,5)-x]-1111, Move up->node:[(1,2)@(2,6)-x]-1111, Move right->node:[(1,2)@(4,6)-z]-1111, Move down->node:[(1,2)@(4,4)-y]-1111, Move left->node:[(1,2)@(3,4)-y]-1111, Move left->node:[(1,2)@(2,4)-y]-1111, Move up->node:[(1,2)@(2,6)-z]-1111, Move right->node:[(1,2)@(3,6)-x]-1111, Move down->node:[(1,2)@(3,5)-x]-1111, Move right->node:[(1,2)@(5,5)-z]-1111, Move right->node:[(1,2)@(6,5)-x]-1111, Move right->node:[(1,2)@(8,5)-z]-1111, Move down->node:[(1,2)@(8,3)-y]-1111, Move down->node:[(1,2)@(8,2)-z]-1111, Move up->node:[(1,2)@(8,3)-y]-11111, Move up->node:[(1,2)@(8,5)-z]-11111, Move left->node:[(1,2)@(6,5)-x]-11111, Move left->node:[(1,2)@(5,5)-z]-11111, Move left->node:[(1,2)@(3,5)-x]-11111, Move up->node:[(1,2)@(3,6)-x]-11111, Move left->node:[(1,2)@(2,6)-z]-11111, Move down->node:[(1,2)@(2,4)-y]-11111, Move right->node:[(1,2)@(3,4)-y]-11111, Move up->node:[(1,2)@(3,6)-z]-11111, Move left->node:[(1,2)@(1,6)-x]-11111, Move left->node:[(1,2)@(0,6)-z]-11111, Move down->node:[(1,2)@(0,4)-y]-11111, Move down->node:[(1,2)@(0,3)-z]-11111, Move down->node:[(1,2)@(0,1)-y]-11111, Move down->node:[(1,2)@(0,0)-z]-11111, Move right->node:[(1,2)@(1,0)-x]-11111, Move right->node:[(1,2)@(3,0)-z]-11111, Move left->node:[(1,2)@(1,0)-x]-111111, Move left->node:[(1,2)@(0,0)-z]-111111, Move up->node:[(1,2)@(0,1)-y]-111111, Move up->node:[(1,2)@(0,3)-z]-111111, Move up->node:[(1,2)@(0,4)-y]-111111, Move up->node:[(1,2)@(0,6)-z]-111111, Move right->node:[(1,2)@(1,6)-x]-111111, Move right->node:[(1,2)@(3,6)-z]-111111, Move down->node:[(1,2)@(3,4)-y]-111111, Move right->node:[(1,2)@(4,4)-y]-111111, Move up->node:[(1,2)@(4,6)-z]-111111, Move left->node:[(1,2)@(2,6)-x]-111111, Move down->node:[(1,2)@(2,5)-x]-111111, Move down->node:[(1,2)@(2,4)-x]-111111, Move right->node:[(1,2)@(4,4)-z]-111111, Move up->node:[(1,2)@(4,5)-y]-111111, Move left->node:[(1,2)@(3,5)-y]-111111, Move down->node:[(1,2)@(3,4)-z]-111111, Move right->node:[(1,2)@(4,4)-x]-111111, Move up->node:[(1,2)@(4,5)-x]-111111, Move right->node:[(1,2)@(6,5)-z]-111111, Move right->node:[(1,2)@(7,5)-x]-111111, Move right->node:[(1,2)@(9,5)-z]-111111, Move right->node:[(1,2)@(10,5)-x]-111111, Move right->node:[(1,2)@(12,5)-z]-111111, Move down->node:[(1,2)@(12,3)-y]-111111, Move down->node:[(1,2)@(12,2)-z]-111111, Move down->node:[(1,2)@(12,0)-y]-111111, Move right->node:[(1,2)@(13,0)-y]-111111, Move up->node:[(1,2)@(13,2)-z]-111111, Move left->node:[(1,2)@(11,2)-x]-111111, Move down->node:[(1,2)@(11,1)-x]-111111, Move down->node:[(1,2)@(11,0)-x]-111111, Move right->node:[(1,2)@(13,0)-z]-111111, Move up->node:[(1,2)@(13,1)-y]-111111, Move left->node:[(1,2)@(12,1)-y]-111111, Move down->node:[(1,2)@(12,0)-z]-111111, Move left->node:[(1,2)@(10,0)-x]-111111, Move up->node:[(1,2)@(10,1)-x]-111111, Move right->node:[(1,2)@(12,1)-z]-111111]"
    //val actions = bloxorz1FormatCondensed(str)
//    val actions = "[R, D, L, U, L, D, R, U, R2, U, R3, U, L, D3, U3, R, D, L3, D, L2, D, L, U, R, D, R, U, L, D, R, U, L, D, R, D2, R, D2, R3, U2, D2, L2, U, L, D, R3, U4, R3]"
//    val level = "/level21.txt"
//    playActionsOnLevel(level, actions)

    reformatBloxorz1Log()
}

private fun playActionsOnLevel(level: String, actions: String) {
    val grid = BloxorzGrid.load(level)
    var completed = false
    try {
        completed = BloxorzSearch.playActionList(grid, BloxorzSearch.expandToActionList(actions))
    } catch (e: Exception) {
        println(e.message)
    }
    println("Playback of $actions in $level was $completed")
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

