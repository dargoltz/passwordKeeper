package util

import com.opencsv.{CSVReader, CSVWriter}

import java.io._
import scala.jdk.CollectionConverters.CollectionHasAsScala
class CSVHandler {

  def getData(csvFile: File): List[List[String]] = {
    val reader = new CSVReader(new FileReader(csvFile))
    val lines = reader.readAll().asScala.map(_.toList).toList
    reader.close()
    lines
  }

  def writeData(csvFile: File, data: List[List[String]]): File = {
    val writer = new CSVWriter(new FileWriter(csvFile))
    data.map(_.toArray).foreach(writer.writeNext)
    writer.close()
    csvFile
  }
}
