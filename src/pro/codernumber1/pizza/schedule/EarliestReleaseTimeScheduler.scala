package pro.codernumber1.pizza.schedule

import scala.util.Sorting.quickSort

/**
  * Warning: This is for use in production!
  * This naive scheduler is present just to be compared to more advanced ones.
  */
object EarliestReleaseTimeScheduler extends Scheduler {
  override def schedule(jobs: Traversable[Job]): Seq[ScheduledJob] = {
    // use a single array of ScheduledJob's to conserve memory
    val result = jobs.view.map(ScheduledJob(_, Long.MaxValue)).toArray
    quickSort(result)(new Ordering[ScheduledJob] {
      override def compare(x: ScheduledJob, y: ScheduledJob): Int = x.releaseTime compare y.releaseTime
    })
    var time: Long = result.headOption.map(_.releaseTime.toLong).getOrElse(0L)
    result.indices foreach { i =>
      if (result(i).releaseTime > time) time = result(i).releaseTime
      result(i) = ScheduledJob(result(i), time)
      time += result(i).processingTime
    }
    result
  }
}
