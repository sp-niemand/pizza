name := "riskident_pizza"

version := "0.0.1"

scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"