package util

import com.opencsv.{CSVReader, CSVWriter}
import model.Note

import java.io._
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala

class CSVHandler {

  def getData(csvFile: File): List[List[String]] = {
    val reader = new CSVReader(new FileReader(csvFile))
    val lines = reader.readAll().asScala.map(_.toList).toList
    reader.close()
    lines
  }

  def writeData(csvFile: File, inputData: Future[List[List[String]]])(implicit ec: ExecutionContext): Future[File] = {
    inputData.map { data =>
      val writer = new CSVWriter(new FileWriter(csvFile))
      try {
        data.foreach(row => writer.writeNext(row.toArray))
      } finally {
        writer.close()
      }
      csvFile
    }
  }
}
