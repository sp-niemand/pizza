package pro.codernumber1.pizza

import pro.codernumber1.pizza.io.Input
import pro.codernumber1.pizza.schedule.{EarliestReleaseTimeScheduler, Job, PSWScheduler}

import scala.collection.mutable
import scala.util.{Failure, Random, Success}

object Main {
  private def randomJobs = {
    val Nb = (100000, 200000)
    val N = Random.nextInt(Nb._2 - Nb._1) + Nb._1
    val L = mutable.ListBuffer.empty[Job]
    val jobLength = 100
    for (i <- 1 to N) {
      L += new Job(Random.nextInt(jobLength), Random.nextInt(jobLength - 1) + 1)
    }
    L
  }

  private def test = {
    import pro.codernumber1.pizza.schedule.ImplicitConversions.Schedule
    1 to 1 foreach { _ =>
      val L = randomJobs
      val r1 = PSWScheduler.schedule(L)
      val r2 = EarliestReleaseTimeScheduler.schedule(L)
      println(r1.averageFinishTime, r2.averageFinishTime)
    }
  }

  def main(args: Array[String]): Unit = {
    import pro.codernumber1.pizza.schedule.ImplicitConversions.Schedule
    (new Input).jobs() match {
      case Success(jobs) => println(PSWScheduler.schedule(jobs).averageFinishTime)
      case Failure(t) => println(s"Input failure: $t")
    }
  }
}