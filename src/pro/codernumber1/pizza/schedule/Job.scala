package pro.codernumber1.pizza.schedule

class Job(val releaseTime: Int, val processingTime: Int) {
  require(releaseTime >= 0)
  require(processingTime >= 0)
  override def toString: String = s"Job(releaseTime = $releaseTime, processingTime = $processingTime)"
}

class ScheduledJob(releaseTime: Int, processingTime: Int, val startTime: Int) extends Job(releaseTime, processingTime) {
  require(processingTime > 0)
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