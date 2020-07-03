package bloxorz

fun main(args: Array<String>) {

    var str = "Kotlin TutorialsepTutorialasepKa\nrtsepExamples\n  ----  \n" +
            "sdfasd\n" +
            "fsdfsadadsf\n" +
            "afsdfsdf"
    var delimiter1 = "sep"
    var delimiter2 = "asep"

    val parts = str.split("\\s+-+\\s+\n".toRegex())

    print(parts)
}