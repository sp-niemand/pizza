package pro.codernumber1.pizza.schedule

abstract class BaseJob(val releaseTime: Int, val processingTime: Int) {
  require(releaseTime >= 0)
  require(processingTime > 0)
}

case class Job(override val releaseTime: Int, override val processingTime: Int)
  extends BaseJob(releaseTime, processingTime)

case class ScheduledJob(override val releaseTime: Int, override val processingTime: Int, startTime: Int)
  extends BaseJob(releaseTime, processingTime) {

  require(startTime >= releaseTime)

  def finishTime: Int = startTime + processingTime
  def waitTime: Int = finishTime - releaseTime
}

object ScheduledJob {
  def apply(j: BaseJob, startTime: Int): ScheduledJob = new ScheduledJob(j.releaseTime, j.processingTime, startTime)
}