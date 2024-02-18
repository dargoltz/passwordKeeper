package model

import play.api.libs.json.{Format, JsResult, JsValue, Json}

import java.sql.Timestamp
import java.time.LocalDateTime

case class Note(id: Int = 0, name: String, password: String, lastChanged: Timestamp = Timestamp.valueOf(LocalDateTime.now))

object Note {

  implicit val format: Format[Note] = new Format[Note] {
    override def reads(json: JsValue): JsResult[Note] =
      for {
        name <- (json \ "name").validate[String]
        password <- (json \ "password").validate[String]
      } yield {
        Note(name = name, password = password)
      }

    override def writes(note: Note): JsValue =
      Json.obj(
        "id" -> note.id,
        "name" -> note.name,
        "password" -> note.password,
        "lastChanged" -> note.lastChanged
      )
  }
}