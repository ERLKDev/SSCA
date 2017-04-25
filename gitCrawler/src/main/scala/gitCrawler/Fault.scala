package gitCrawler

/**
  * Created by ErikL on 4/11/2017.
  */
class Fault(commitV: Commit, issuesV: List[Issue]) {

  val commit: Commit = commitV
  val issues: List[Issue] = issuesV
}
