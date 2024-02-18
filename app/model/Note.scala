package model

import play.api.libs.json.{Format, JsResult, JsValue, Json}

import java.time.LocalDateTime

case class Note(id: Option[Int] = None, name: String, password: String, lastChanged: LocalDateTime = LocalDateTime.now) {
  def toList: List[String] = {
    List(id.toString, name, password, lastChanged.toString)
  }
}

object Note {
  //  implicit val localDateTimeFormat: Format[LocalDateTime] = Format(
  //    Reads.localDateTimeReads(DateTimeFormatter.ISO_DATE_TIME),
  //    Writes.temporalWrites(DateTimeFormatter.ISO_DATE_TIME)
  //  )

  implicit val format: Format[Note] = new Format[Note] {
    override def reads(json: JsValue): JsResult[Note] =
      for {
        name <- (json \ "name").validate[String]
        password <- (json \ "password").validate[String]
      } yield {
        Note(name = name, password = password)
      }

    override def writes(o: Note): JsValue =
      Json.obj(
        "name" -> o.name,
        "password" -> o.password
      )
  }
}