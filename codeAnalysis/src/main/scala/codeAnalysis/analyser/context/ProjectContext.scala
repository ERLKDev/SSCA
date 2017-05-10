package main.scala.analyser.context

import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

import codeAnalysis.analyser.AST.AST
import codeAnalysis.analyser.Compiler.CompilerS

import scala.collection.concurrent.TrieMap


/**
  * Created by ErikL on 4/7/2017.
  */
class ProjectContext(val compiler: CompilerS, files: List[File], val cacheEnabled: Boolean, val cacheSize: Int) {
  private val compileCache: TrieMap[File, AST] = TrieMap.empty[File, AST]
  private val cacheFiles = new ConcurrentLinkedQueue[File]()

  def this(compiler: CompilerS, files: List[File], cacheEnabled: Boolean) = {
    this(compiler, files, cacheEnabled, 20)
  }

  def this(compiler: CompilerS, files: List[File]) = {
    this(compiler, files, false, 0)
  }

  def getFiles : List[File] = files


  def isCached(file: File) : Boolean = {
    compileCache.contains(file)
  }

  def getCached(file: File) : Option[AST] = {
    compileCache.get(file)
  }

  def addPreCompiledFile(file: File, ast: AST) : Unit = {
    if (compileCache.size + 1 > cacheSize)
      compileCache.drop(1)
    compileCache += (file -> ast)
  }

  def addFileToCache(file: File): Boolean = {
    cacheFiles.add(file)
  }

  def removeFileToCache(file: File): Boolean = {
    cacheFiles.remove(file)
  }

  def clearFilesToCache(): Unit = {
    cacheFiles.clear()
  }

  def shouldCache(file: File): Boolean = {
    cacheFiles.contains(file)
  }
}
