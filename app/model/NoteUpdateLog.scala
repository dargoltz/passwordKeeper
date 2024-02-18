package model

import play.api.libs.json.{Format, Json}

import java.sql.Timestamp

case class NoteUpdateLog(id: Int, noteId: Int, action: String, changed: Timestamp, newPassword: Option[String], oldPassword: Option[String])

object NoteUpdateLog {
  implicit val format: Format[NoteUpdateLog] = Json.format[NoteUpdateLog]
}