package gitCrawler.util

import java.io.File

import dispatch.github.GhCommit
import net.liftweb.json.Extraction._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Printer._

import scala.io.Source

/**
  * Created by erikl on 4/20/2017.
  */
class GitDataBase(repoPath: String) {

  implicit val formats = net.liftweb.json.DefaultFormats

  val dir = new File(repoPath + "Data\\")
  if(!dir.exists())
    dir.mkdirs()

  /**
    * Writes the commit data to a file
    *
    * @param obj the commit
    */
  def writeCommit(obj: GhCommit): Unit = {
    val json = pretty(render(decompose(obj)))
    write(obj.sha, json)
  }

  /**
    * Reads the commit data from a file
    *
    * @param id the commit id
    * @return
    */
  def readCommit(id: String): Option[GhCommit] = {
    read(id) match {
      case Some(data) =>
        Some(net.liftweb.json.parse(data).extract[GhCommit])
      case _ =>
        None
    }
  }


  protected def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }


  private def write(id: String, data: String): Unit = {
    val file = new File(repoPath + "Data\\" + id)
    printToFile(file) { p =>
      p.println(data)
    }
  }


  private def read(id: String): Option[String] = {
    val fp = new File(repoPath + "Data\\" + id)
    if (!fp.exists())
      return None

    val file = Source.fromFile(fp)
    try {
      val result = Some(file.mkString)
      file.close()
      result
    }catch {
      case _=>
        file.close()
        None
    }
  }
}
