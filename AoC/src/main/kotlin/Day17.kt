import Day17.part1
import Day17.part2

fun main() {
    part1()
    part2()
}

object Day17 {
    fun part1() {
        var highestY = 0
        for (y in 1..200) {
            for (x in 1..173) {
                val (shotResult, canHit) = shoot(
                    xSpeed = x,
                    ySpeed = y,
                    targetX = 150..171,
                    targetY = -129..-70
                )
                if (canHit) {
                    if (highestY < shotResult.maxY) highestY = shotResult.maxY
                    println("Height of $y can hit $shotResult")
                }
            }
        }
        println("highestY :$highestY")
    }

    fun part2() {
        val speedsThatCanHit = (-131..200).flatMap { y ->
            (1..173).map { x ->
                val (shotResult, canHit) = shoot(
                    xSpeed = x,
                    ySpeed = y,
                    targetX = 150..171,
                    targetY = -129..-70
                )
                shotResult to canHit
            }
        }
        println("speeds that can hit :${speedsThatCanHit.count { it.second }}")
    }

    fun shoot(xSpeed: Int, ySpeed: Int, targetX: IntRange, targetY: IntRange): Pair<ShotResult, Boolean> {
        var shotResult: ShotResult = ShotResult(0, 0, xSpeed, ySpeed)
        do {
            shotResult = shoot(shotResult)
        } while (!achieved(shotResult, targetX, targetY) && achievable(shotResult, targetX, targetY))
        return shotResult to (shotResult.xPosition in targetX && shotResult.yPosition in targetY)
    }

    private fun achievable(shotResult: ShotResult?, targetX: IntRange, targetY: IntRange) =
        shotResult?.run {
            this.canHitX(targetX) && this.canHitY(targetY)
        } ?: throw Error("Unexpected null ShotResult")

    private fun ShotResult.canHitX(targetX: IntRange) = if (xPosition > targetX.last) false
    else if (xPosition in targetX) true
    else (1..xSpeed).sum() + xPosition >= targetX.first

    private fun ShotResult.canHitY(targetY: IntRange) =
        yPosition >= targetY.first

    private fun achieved(shotResult: ShotResult?, targetX: IntRange, targetY: IntRange) =
        shotResult?.run { xPosition in targetX && yPosition in targetY }
            ?: throw Error("Shot position should not be null")

    fun shoot(shotResult: ShotResult) = shotResult.run {
        val newXPosition = xPosition + xSpeed
        val newyPosition = yPosition + ySpeed
        val newXSpeed = bringToZero(xSpeed)
        val newYSpeed = applyGravity(ySpeed)
        val maxYPosition = maxOf(shotResult.maxY, newyPosition)
        ShotResult(newXPosition, newyPosition, newXSpeed, newYSpeed, maxYPosition)
    }

    data class ShotResult(
        val xPosition: Int,
        val yPosition: Int,
        val xSpeed: Int,
        val ySpeed: Int,
        val maxY: Int = yPosition
    )

    private fun applyGravity(ySpeed: Int) = ySpeed - 1

    private fun bringToZero(xSpeed: Int) = when {
        xSpeed > 0 -> xSpeed - 1
        xSpeed < 0 -> xSpeed + 1
        else -> 0
    }
}