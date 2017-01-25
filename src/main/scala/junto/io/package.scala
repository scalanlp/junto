package junto

import java.io._
import java.util.zip._
import scala.io.Source.{ fromFile, fromInputStream }
import scala.io.BufferedSource

package object io {

  // This allows reading to ignore malformed characters and keep reading
  // without throwing an exception and stopping.
  import java.nio.charset.CodingErrorAction
  import scala.io.Codec
  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  def createWriter(filename: String): Writer =
    createWriter(new File(filename))

  def createWriter(parentDir: File, filename: String): Writer =
    createWriter(new File(parentDir, filename))

  def createWriter(file: File): Writer =
    if (isgz(file)) gzipWriter(file) else new BufferedWriter(new FileWriter(file))

  def createDataOutputStream(parentDir: File, filename: String): DataOutputStream =
    new DataOutputStream(new GZIPOutputStream(
      new FileOutputStream(new File(parentDir, filename))
    ))

  def gzipWriter(file: File): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(
      new GZIPOutputStream(new FileOutputStream(file))
    ))

  def writeStringIntPair(dos: DataOutputStream, word: String, value: Int) {
    dos.writeUTF(word)
    dos.writeInt(value)
  }

  def writePair[V](writer: Writer, word: String, value: V) {
    writer.write(word + "\t" + value + "\n")
  }

  def getWordCounts(inputFile: File, mincount: Int = 0) = {

    val postSource =
      if (isgz(inputFile)) gzipSource(inputFile) else fromFile(inputFile)

    val wordCounts = new collection.mutable.HashMap[String, Int]().withDefaultValue(0)
    for (token <- postSource.getLines)
      wordCounts(token) += 1

    for ((word, count) <- wordCounts; if count > mincount) yield (word, count)
  }

  def readCounts(countsFile: File) = fromFile(countsFile)
    .getLines
    .map { line => val Array(w, c) = line.split("\t"); (w, c.toInt) }
    .toMap

  def readScores(countsFile: File) = fromFile(countsFile)
    .getLines
    .map { line => val Array(w, s) = line.split("\t"); (w, s.toDouble) }
    .toMap

  def getSource(file: String): BufferedSource =
    getSource(new File(file))

  def getSource(file: File): BufferedSource =
    if (isgz(file)) gzipSource(file) else fromFile(file)

  def gzipSource(file: String): BufferedSource =
    gzipSource(new File(file))

  def gzipSource(file: File): BufferedSource = {
    fromInputStream(new GZIPInputStream(new FileInputStream(file)))
  }

  def isgz(file: File): Boolean =
    file.getName.endsWith(".gz")

}
