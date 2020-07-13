package bloxorz

import bloxorz.Formatting.expandToActionList
import bloxorz.Game.isAtSink

object Scratch

fun main(args: Array<String>) {
    //runPostSolution()
    //runBloxorz1Moves()
    //Main.allRoutesForLevel(17)
    (1..20).asSequence().forEach {
        try {
            val r = Main.searchForwardsThenBackwards(it)
            val forwardMoves = Formatting.condensedFormat(r.first.path)
            val reverseMoves = Formatting.condensedFormat(r.second.path)
            println("Level $it forward : ${r.first.path.history.size} : $forwardMoves")
            println("Level $it reverse : ${r.second.path.history.size} : $reverseMoves")
        } catch(e:Exception) {
            println("Level $it : $e")
        }
    }
}

private fun runPostSolution() {
    val all = mutableListOf<Game.Action>()

    Scratch.javaClass.getResource("/postedsolution.txt")
        .readText()
        .lines()
        .chunked(3)
        .forEach {
            all.addAll(playActionsOnLevel("/level${it[0].split(" ")[1].trim().toInt()}.txt", it[1].trim()))
        }

    println("Total actions ${all.count()}, with ${all.count { it != Game.Action.SwitchBlock }}")
}

private fun playActionsOnLevel(level: String, actions: String) :List<Game.Action> {
    val actionList = expandToActionList(actions)
    var completed = false
    try {
        completed = isAtSink(Grid.load(level), Main.playback(Grid.load(level), actionList))
    } catch (e: Exception) {
        println("Failed to load $level : $e")
    }
    println("Level $level with ${actionList.count()} moves was $completed. Playback of $actions")
    return actionList
}


private fun runBloxorz1Moves() {

    val all = mutableListOf<Game.Action>()

    var levelId = 1

    Scratch.javaClass.getResource("/bloxorz1-result.txt")
        .readText()
        .lines()
        .forEach {
            val level = "/level$levelId.txt"
            val actions = playActionsOnLevel(level, it)
            all.addAll(actions)
            levelId++
        }

    println("Total actions ${all.count()}, with ${all.count { it != Game.Action.SwitchBlock }}")
}

