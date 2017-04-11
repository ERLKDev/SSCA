package main.scala
/**
  * Created by ErikL on 4/11/2017.
  */
class Fault(commitV: Commit, issuesV: List[Issue]) {

  val commit: Commit = commitV
  def issues: List[Issue] = issuesV
}
