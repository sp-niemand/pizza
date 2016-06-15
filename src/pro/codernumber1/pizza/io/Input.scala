package pro.codernumber1.pizza.io

import pro.codernumber1.pizza.schedule.Job

import scala.io.Source
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

object Input {
  private val source = Source.stdin

  private def lineToJob(line: String): Job = {
    val parts = line.split(' ')
    require(parts.length == 2)
    new Job(parts.head.toInt, parts.last.toInt)
  }

  def jobs(): Try[Seq[Job]] = {
    val lines = source.getLines()
    Try(lines.next().toInt) recoverWith {
      case NonFatal(t) => Failure(new RuntimeException("Wrong job count given", t))
    } flatMap { jobCount: Int =>
      Try(lines.map(lineToJob).take(jobCount).toArray)
    }
  }
}

