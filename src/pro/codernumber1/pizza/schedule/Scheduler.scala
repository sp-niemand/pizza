package pro.codernumber1.pizza.schedule

trait Scheduler {
  def schedule(jobs: Traversable[Job]): Seq[ScheduledJob]
}