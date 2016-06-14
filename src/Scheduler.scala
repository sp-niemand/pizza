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
}

class Scheduler {
  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Double = s.map(_.waitTime).sum / s.length
    def finish: Int = s.tail.finish
  }

  /**
    * SRPT algorithm for scheduling with preemption
    * http://www.stern.nyu.edu/om/faculty/pinedo/scheduling/shakhlevich/handout03.pdf
    */
  private def scheduleUsingSrpt(jobs: Traversable[Job]): Seq[Job] = {
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
    val jobsCompleted: mutable.ListBuffer[PreemptiveJob] = mutable.ListBuffer.empty

    val iterator = releaseTimes.iterator.buffered
    var time: Int = -1
    var currentJob: Option[PreemptiveJob] = None

    def nextEventTime: Int = currentJob
      .map(j => Math.min(time + j.processingTimeLeft, iterator.head))
      .getOrElse(iterator.head)

    def incTimeToNextEvent(): Boolean = {
      val currentJobFinish: Int = currentJob.map(_.processingTimeFinish).getOrElse(Int.MaxValue)
      val nextReleaseTime: Int = Try(iterator.head).getOrElse(Int.MaxValue)
      val nextEventTime = Math.min(currentJobFinish, nextReleaseTime)
      val existsNextEvent = nextEventTime != Int.MaxValue
      if (existsNextEvent) {
        currentJob foreach(_.processingTimeLeft -= nextEventTime - time)
        time = nextEventTime
        if (iterator.hasNext) iterator.next() // proceed to the next release time
      }
      existsNextEvent
    }

    while (incTimeToNextEvent()) {
      // work with current job
      currentJob foreach { j =>
        require(j.processingTimeLeft >= 0)
        currentJob = None
        if (j.processingTimeLeft == 0) {
          // if current job is fully processed, mark it as finished
          jobsCompleted += j
        } else {
          // if not fully processed, return it to the jobs pool
          jobsReleased.enqueue(j)
        }
      }

      // check if any jobs are ready to be started
      while(jobsRemaining.nonEmpty && jobsRemaining.top.releaseTime <= time) {
        jobsReleased.enqueue(jobsRemaining.pop)
      }

      // start processing a job with shortest remaining processing time
      if (jobsReleased.nonEmpty) {
        val j = jobsReleased.dequeue()
        j.processingTimeStart = time
        currentJob = Some(j)
      }
    }

    jobsCompleted
  }

  def schedule(jobs: Traversable[Job]): Seq[Job] = {
    scheduleUsingSrpt(jobs)
  }
}