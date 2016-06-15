package pro.codernumber1.pizza.schedule

import scala.collection.{SortedSet, mutable}
import scala.util.Try

/**
  * Uses PSW (Phillips, C.; Stein, C. & Wein, J) scheduling algorithm for 1 machine.
  *
  * First SRPT (shortest remaining processing time) rule is used to schedule tasks as if they were preemptive.
  * Then tasks get actually scheduled in order of decreasing completion times according to this temporary schedule.
  */
object PSWScheduler extends Scheduler {
  class PreemptiveJob(releaseTime: Int, processingTime: Int) extends Job(releaseTime, processingTime) {
    var processingTimeLeft: Int = processingTime
    var processingTimeStart: Int = Int.MinValue
    def processingTimeFinish: Int = processingTimeStart + processingTimeLeft
  }

  /**
    * SRPT algorithm for scheduling with preemptionj.start
    * http://www.stern.nyu.edu/om/faculty/pinedo/scheduling/shakhlevich/handout03.pdf
    */
  def schedule(jobs: Traversable[Job]): Seq[ScheduledJob] = {
    val releaseTimes: Iterable[Int] = jobs.view.map(_.releaseTime).to[SortedSet]
    val releaseTimeIterator = releaseTimes.iterator.buffered

    implicit val remainingProcessingTimeOrdering = new Ordering[PreemptiveJob] {
      override def compare(x: PreemptiveJob, y: PreemptiveJob): Int = y.processingTimeLeft compare x.processingTime
    }
    val jobsRemaining: mutable.Stack[PreemptiveJob] =
      jobs.view.map(j => new PreemptiveJob(j.releaseTime, j.processingTime))
        .to[mutable.Stack].sortBy(_.releaseTime)

    val jobsReleased: mutable.PriorityQueue[PreemptiveJob] = mutable.PriorityQueue.empty[PreemptiveJob]

    var resultSize: Int = 0
    val result: Array[ScheduledJob] = new Array(jobs.size)

    var time: Int = -1
    var currentJob: Option[PreemptiveJob] = None

    // could use ImplicitConversions.Schedule.finish instead, but this "cache" is more optimal
    var resultingScheduleFinishTime: Int = 0
    def finishJob(job: PreemptiveJob): Unit = {
      val scheduledJob = ScheduledJob(job, Math.max(job.releaseTime, resultingScheduleFinishTime))
      result(resultSize) = scheduledJob
      resultSize += 1
      resultingScheduleFinishTime = scheduledJob.finishTime
    }

    def incTimeToNextEvent(): Boolean = {
      val Never = Int.MaxValue
      val currentJobFinish: Int = currentJob.map(_.processingTimeFinish).getOrElse(Never)
      val nextReleaseTime: Int = Try(releaseTimeIterator.head).getOrElse(Never)
      val nextEventTime = Math.min(currentJobFinish, nextReleaseTime)
      val existsNextEvent = nextEventTime != Never
      if (existsNextEvent) {
        currentJob foreach(_.processingTimeLeft -= nextEventTime - time)
        time = nextEventTime
        if (releaseTimeIterator.hasNext) releaseTimeIterator.next() // proceed to the next release time
      }
      existsNextEvent
    }

    while (incTimeToNextEvent()) {
      // work with current job
      currentJob foreach { job =>
        require(job.processingTimeLeft >= 0)
        currentJob = None
        if (job.processingTimeLeft == 0) {
          // if current job is fully processed, mark it as finished and add it to the resulting schedule
          finishJob(job)
        } else {
          // if not fully processed, return it to the jobs pool
          jobsReleased.enqueue(job)
        }
      }

      // check if any jobs are ready to be started
      while(jobsRemaining.nonEmpty && jobsRemaining.top.releaseTime <= time) {
        jobsReleased.enqueue(jobsRemaining.pop)
      }

      // start processing a job with shortest remaining processing time
      if (jobsReleased.nonEmpty) {
        val job = jobsReleased.dequeue()
        job.processingTimeStart = time
        currentJob = Some(job)
      }
    }

    result
  }
}