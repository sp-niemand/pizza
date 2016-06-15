package pro.codernumber1.pizza

import pro.codernumber1.pizza.schedule.{EarliestReleaseTimeScheduler, Job, PSWScheduler}

import scala.collection.mutable
import scala.util.Random

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

  def main(args: Array[String]): Unit = {
    import pro.codernumber1.pizza.schedule.ImplicitConversions.Schedule

    val smartScheduler = new PSWScheduler
    val naiveScheduler = new EarliestReleaseTimeScheduler
    1 to 1 foreach { _ =>
      val L = randomJobs
      val r1 = smartScheduler.schedule(L)
      val r2 = naiveScheduler.schedule(L)
      println(r1.averageFinishTime, r2.averageFinishTime)
    }
  }
}