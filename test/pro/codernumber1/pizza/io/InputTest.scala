package pro.codernumber1.pizza.io

import pro.codernumber1.pizza.schedule.Job
import pro.codernumber1.pizza.testutil.UnitTest

import scala.io.Source
import scala.util.{Failure, Try}

class InputTest extends UnitTest {
  class Fixture {
    var text: String = ""
    def jobs: Try[Array[Job]] = new Input(Source.fromString(text.stripMargin)).jobs()
    def assertFailure() = jobs shouldBe a [Failure[_]]
  }

  "Input" should "read data in the right format" in new Fixture {
    text =
      """3
        |1 2
        |3 6
        |0 8
      """
    jobs.toOption contains Array(
      Job(1, 2),
      Job(3, 6),
      Job(0, 8)
    )
  }

  it should "work for empty collection of jobs" in new Fixture {
    text = "0"
    jobs.get shouldBe empty
  }

  it should "fail for jobs with negative time parameters" in new Fixture {
    text =
      """2
        |4 2
        |-2 8
      """.stripMargin
    assertFailure()

    text =
      """2
        |4 -2
        |2 8
      """.stripMargin
    assertFailure()
  }

  it should "fail for wrong job count" in new Fixture {
    text =
      """3
        |4 2
        |2 8
      """.stripMargin
    assertFailure()
  }

  it should "fail if letters are used somewhere" in new Fixture {
    text =
      """2
        |a 2
        |2 8
      """.stripMargin
    assertFailure()
  }

  it should "fail for an empty input" in new Fixture {
    text = ""
    assertFailure()
  }

  it should "fail if more than one space used as a divider" in new Fixture {
    text =
      """2
        |1 2
        |2  8
      """.stripMargin
    assertFailure()
  }
}
