package model

import play.api.libs.json.{Format, Json}

import java.time.LocalDateTime

case class NoteUpdateLog(id: Option[Int], noteId: Int, action: String, changed: LocalDateTime, newPassword: Option[String], oldPassword: Option[String])

object NoteUpdateLog {
  implicit val format: Format[NoteUpdateLog] = Json.format[NoteUpdateLog]
}