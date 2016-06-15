package pro.codernumber1.pizza.schedule

import scala.util.Sorting.quickSort

object EarliestReleaseTimeScheduler extends Scheduler {
  override def schedule(jobs: Traversable[Job]): Seq[ScheduledJob] = {
    // use a single array of ScheduledJob's to conserve memory
    val result = jobs.view.map(ScheduledJob(_, Int.MaxValue)).toArray
    quickSort(result)(new Ordering[ScheduledJob] {
      override def compare(x: ScheduledJob, y: ScheduledJob): Int = x.releaseTime compare y.releaseTime
    })
    var time: Int = result.headOption.map(_.releaseTime).getOrElse(0)
    result.indices foreach { i =>
      if (result(i).releaseTime > time) time = result(i).releaseTime
      result(i) = ScheduledJob(result(i), time)
      time += result(i).processingTime
    }
    result
  }
}
