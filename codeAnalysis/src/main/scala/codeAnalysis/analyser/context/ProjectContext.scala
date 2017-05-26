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

  /**
    * Constructor with a standard cache size
    *
    * @param compiler
    * @param files
    * @param cacheEnabled
    */
  def this(compiler: CompilerS, files: List[File], cacheEnabled: Boolean) = {
    this(compiler, files, cacheEnabled, 20)
  }

  /**
    * Constructor with the cache disabled
    *
    * @param compiler
    * @param files
    */
  def this(compiler: CompilerS, files: List[File]) = {
    this(compiler, files, false, 0)
  }

  /**
    * Get the list of project files
    *
    * @return Project files
    */
  def getFiles : List[File] = files


  /**
    * Checks if a file is cached in the project context
    *
    * @param file
    * @return
    */
  def isCached(file: File) : Boolean = {
    compileCache.contains(file)
  }

  /**
    * Get a cached file
    *
    * @param file
    * @return
    */
  def getCached(file: File) : Option[AST] = {
    compileCache.get(file)
  }

  /**
    * Adds a precompiled file to the cache
    * @param file
    * @param ast
    */
  def addPreCompiledFile(file: File, ast: AST) : Unit = {
    if (cacheEnabled) {
      if (compileCache.size + 1 > cacheSize)
        compileCache.drop(1)
      compileCache += (file -> ast)
    }
  }

  /**
    * Adds a file the compiler should cache on compile time
    *
    * @param file
    * @return
    */
  def addFileToCache(file: File): Boolean = {
    cacheFiles.add(file)
  }

  /**
    * Removes a file the compiler should cache on compile time
    * @param file
    * @return
    */
  def removeFileToCache(file: File): Boolean = {
    cacheFiles.remove(file)
  }

  /**
    * Clear all the files the compiler should cache
    */
  def clearFilesToCache(): Unit = {
    cacheFiles.clear()
  }

  /**
    * Checks if the compiler should cache the file
    *
    * @param file
    * @return
    */
  def shouldCache(file: File): Boolean = {
    cacheFiles.contains(file)
  }
}
