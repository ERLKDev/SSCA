package gitCrawler

/**
  * Created by ErikL on 4/11/2017.
  */
class Fault(commitV: Commit, issuesV: List[Issue]) {

  var commit: Commit = commitV
  var issues: List[Issue] = issuesV

  def unload(): Unit = {
    commit = null
    issues = null
  }
}
