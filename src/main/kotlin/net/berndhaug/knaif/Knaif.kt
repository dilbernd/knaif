package net.berndhaug.knaif

import java.io.File
import java.nio.file.*
import java.util.logging.Logger
import kotlin.system.exitProcess

val LOGGER = Logger.getLogger("Knaif")

data class Duration(val from: Int, val to: Int) {
    companion object {
        fun fromLine(line: String): Duration {
            val startEnd = secondsFromTimeVals(timeValsFromDurationLine(line)).subList(0, 2)
            return Duration(startEnd.get(0), startEnd.get(1))
        }

        fun timeValsFromDurationLine(line: String) = line.split("-").map { it.trim().split(":").map(String::toInt) }
        fun secondsFromTimeVals(vals: List<List<Int>>) = vals.map { it.reduce { a, b -> a * 60 + b } }
    }
}

fun durationsFromCuttingDirections(cuttingDirectionsFileName: String): List<Duration> {
    val stream = Files.newInputStream(Paths.get(cuttingDirectionsFileName))
    stream.buffered().reader().use { reader ->
        return durationsFromCuttingDirectionsContent(reader.readText())
    }
}

fun durationsFromCuttingDirectionsContent(contents: String): List<Duration> {
    return contents.split("\n").map(String::trim).filter { it != "" }.map(Duration.Companion::fromLine)
}

fun invertDurations(durations: List<Duration>): List<Duration> {
    fun invert(soFar: List<Duration>, nextStart: Int, remaining: List<Duration>): List<Duration> {
        return if (remaining.isEmpty()) {
            soFar + Duration(nextStart, Int.MAX_VALUE)
        } else {
            val nextDuration = remaining.get(0)
            invert(soFar + Duration(nextStart, nextDuration.from),
                    nextDuration.to + 1,
                    remaining.subList(1, remaining.size))
        }
    }
    return invert(listOf(), 0, durations)
}

fun makeCommands(inputName: String, sequences: List<Duration>): String {
    return sequences.flatMap { dur ->
        // TODO: duration for seekability?
        listOf("file $inputName"
                , if (dur.from != 0) "inpoint ${dur.from}" else ""
                , if (dur.to != Int.MAX_VALUE) "outpoint ${dur.to}" else "")
    }.joinToString(separator = "\n", prefix = "ffconcat version 1.0\n")
}

fun writeCommandsFile(inputPath: Path, contents: String): File {
    val inputBasename = inputPath.fileName
    val inputDir = inputPath.parent ?: FileSystems.getDefault().getPath(".")
    val directionsPath = inputDir.resolve("$inputBasename.cutDecisions")
    Files.newOutputStream(directionsPath, *arrayOf()).buffered().writer().use {
        it.write(contents)
    }
    return directionsPath.toFile()
}

fun runExtract(commandFile: File, outputName: String) =
        ProcessBuilder("ffmpeg",
                "-f",
                "concat",
                "-i",
                commandFile.canonicalPath,
                outputName).inheritIO().start().waitFor() != 0

fun cleanUp(commands: File): Boolean {
    return commands.delete()
}

fun main(args: Array<String>) {
    if (args.size != 3) {
        System.err.println("Invalid # of arguments. Usage: <in vid> <cut decisions file> <out vid>")
        exitProcess(1)
    }
    val (inFile, cutFile, outFile) = args
    LOGGER.info("Cutting [$inFile] into [$outFile] according to [$cutFile].")

    val inputPath = FileSystems.getDefault().getPath(inFile)
    val inputBasename = inputPath.fileName
    val inputDirName = inputPath.parent ?: "."

    val cutDurations = durationsFromCuttingDirections(cutFile)
    val keepDurations = invertDurations(cutDurations)
    val commands = makeCommands(inputPath.fileName.toString(), keepDurations)
    val commandsFile = writeCommandsFile(inputPath, commands)
    runExtract(commandsFile, outFile)
    if (!cleanUp(commandsFile)) {
        LOGGER.warning("Could not delete cut command file ${commandsFile.canonicalPath}")
    }
}
