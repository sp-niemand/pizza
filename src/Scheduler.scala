import scala.collection.mutable

trait Job {
  def added: Int
  def length: Int
}

class Scheduler[T <: Job] {
  case class ScheduledJob(job: T, start: Int) {
    require(job.length >= 0)
    require(job.added >= 0)
    require(start >= job.added)

    def finish: Int = start + job.length
    def waitTime: Int = finish - job.added
  }

  implicit class Schedule(s: Seq[ScheduledJob]) {
    def averageFinishTime: Double = s.map(_.waitTime).sum / s.length
    def finish: Int = s.tail.finish
  }

  private class Node(val schedule: Seq[ScheduledJob], left: Traversable[T]) {
    def betterThan(that: Node): Boolean = this.schedule.averageFinishTime < that.schedule.averageFinishTime

    def isLeaf: Boolean = left.isEmpty

    def children: Traversable[Node] = {
      left.map { job =>
        new Node(
          schedule :+ ScheduledJob(job, Math.max(schedule.finish, job.added)),
          left filterNot (_ == job) // TODO: enforce adequate equality check for jobs
        )
      }
    }
  }

  def schedule(jobs: Traversable[T]): Seq[ScheduledJob] = {
    val stack: mutable.Stack[Node] = mutable.Stack(new Node(Seq.empty, jobs))
    var best: Node = null // use null because Option is excessive here
    while (stack.nonEmpty) {
      val node = stack.pop()
      val bestTime = if (best == null) Double.PositiveInfinity else best.schedule.averageFinishTime
      val nodeTime = node.schedule.averageFinishTime
      if (node.isLeaf) {
        if (nodeTime < bestTime) {
          best = node
        } else {
          best
        }
      } else {
        if (nodeTime < bestTime) node.children foreach stack.push
      }
    }
    best.schedule
  }
}