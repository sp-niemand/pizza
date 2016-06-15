package pro.codernumber1.pizza.schedule

import pro.codernumber1.pizza.testutil.UnitTest

import scala.util.Random

class PSWSchedulerTest extends UnitTest {
  class Fixture {
    var jobs: Traversable[Job] = Array.empty[Job]
    def schedule: List[ScheduledJob] = PSWScheduler.schedule(jobs).toList

    def randomJobs(jobCount: Int, maxJobLength: Int, maxReleaseTime: Int): Traversable[Job] =
      (0 until jobCount).view map { _ =>
        val releaseTime = Random.nextInt(maxReleaseTime)
        val processingTime = Random.nextInt(maxJobLength)
        Job(releaseTime, processingTime)
      }
  }

  "PSWScheduler" should "work" in new Fixture {
    jobs = Array(
      Job(0, 1),
      Job(1, 2),
      Job(2, 3)
    )
    schedule shouldBe List(
      ScheduledJob(0, 1, 0),
      ScheduledJob(1, 2, 1),
      ScheduledJob(2, 3, 3)
    )

    jobs = Array(
      Job(1, 10),
      Job(2, 7),
      Job(3, 4),
      Job(3, 5)
    )
    schedule shouldBe List(
      ScheduledJob(3, 4, 3),
      ScheduledJob(3, 5, 7),
      ScheduledJob(2, 7, 12),
      ScheduledJob(1, 10, 19)
    )
  }

  it should "work if empty" in new Fixture {
    jobs = Nil
    schedule shouldBe empty
  }

  it should "work for 100000 random jobs in satisfying time" in new Fixture {
    import ImplicitConversions.Schedule

    import scala.concurrent.duration._

    jobs = randomJobs(100000, 1000000000, 1000000000)
    val deadline = 30 seconds fromNow
    val s = schedule
    deadline should not be 'isOverdue
    s should have size 100000
    s.averageFinishTime should be > 0L
  }
}
