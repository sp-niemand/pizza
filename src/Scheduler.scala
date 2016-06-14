import scala.collection.{SortedSet, mutable}
import scala.util.Try

class Job(val releaseTime: Int, val processingTime: Int) {
  require(releaseTime >= 0)
  require(processingTime >= 0)
  override def toString: String = s"Job(releaseTime = $releaseTime, processingTime = $processingTime)"
}

class ScheduledJob(releaseTime: Int, processingTime: Int, val startTime: Int) extends Job(releaseTime, processingTime) {
  require(processingTime >= 0)
  require(releaseTime >= 0)
  require(startTime >= releaseTime)

  def finishTime: Int = startTime + processingTime
  def waitTime: Int = finishTime - releaseTime
  override def toString: String = s"ScheduledJob(releaseTime = $releaseTime, processingTime = $processingTime, " +
    s"startTime = $startTime)"
}

object ScheduledJob {
  def apply(j: Job, startTime: Int): ScheduledJob = new ScheduledJob(j.releaseTime, j.processingTime, startTime)
}

class Scheduler {
  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Double = s.map(_.waitTime).sum / s.length
    def finish: Int = s.lastOption.map(_.finishTime).getOrElse(0)
  }

  /**
    * SRPT algorithm for scheduling with preemptionj.start
    * http://www.stern.nyu.edu/om/faculty/pinedo/scheduling/shakhlevich/handout03.pdf
    */
  def schedule(jobs: Traversable[Job]): Seq[ScheduledJob] = {
    class PreemptiveJob(releaseTime: Int, processingTime: Int) extends Job(releaseTime, processingTime) {
      var processingTimeLeft: Int = processingTime
      var processingTimeStart: Int = Int.MinValue
      def processingTimeFinish: Int = processingTimeStart + processingTimeLeft
    }
    val releaseTimes: Iterable[Int] = jobs.view.map(_.releaseTime).to[SortedSet]

    implicit val remainingProcessingTimeOrdering = new Ordering[PreemptiveJob] {
      override def compare(x: PreemptiveJob, y: PreemptiveJob): Int = y.processingTimeLeft compare x.processingTime
    }

    val jobsRemaining: mutable.Stack[PreemptiveJob] =
      jobs.view.map(j => new PreemptiveJob(j.releaseTime, j.processingTime))
        .to[mutable.Stack].sortBy(_.releaseTime)
    val jobsReleased: mutable.PriorityQueue[PreemptiveJob] = mutable.PriorityQueue.empty[PreemptiveJob]
    val result: mutable.ListBuffer[ScheduledJob] = mutable.ListBuffer.empty

    val releaseTimeIterator = releaseTimes.iterator.buffered
    var time: Int = -1
    var currentJob: Option[PreemptiveJob] = None

    def finishJob(job: PreemptiveJob): Unit =
      result += ScheduledJob(job, Math.max(job.releaseTime, result.finish))

    def nextEventTime: Int = currentJob
      .map(j => Math.min(time + j.processingTimeLeft, releaseTimeIterator.head))
      .getOrElse(releaseTimeIterator.head)

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