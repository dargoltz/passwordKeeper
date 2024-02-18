package model

import play.api.libs.json.{Format, JsResult, JsValue, Json}

import java.sql.Timestamp

case class NoteUpdateLog(id: Int, noteId: Int, action: String, changed: Timestamp, newPassword: Option[String], oldPassword: Option[String])

object NoteUpdateLog {

  implicit val format: Format[NoteUpdateLog] = new Format[NoteUpdateLog] {

    override def writes(log: NoteUpdateLog): JsValue =
      Json.obj(
        "id" -> log.id,
        "note_id" -> log.noteId,
        "action" -> log.action,
        "changed" -> log.changed.toString,
        "new_password" -> log.newPassword,
        "old_password" -> log.oldPassword
      )

    override def reads(json: JsValue): JsResult[NoteUpdateLog] =
      for {
        noteId <- (json \ "note_id").validate[String]
        action <- (json \ "action").validate[String]
        changed <- (json \ "changed").validate[String]
        newPassword <- (json \ "new_password").validate[String]
        oldPassword <- (json \ "old_password").validate[String]
      } yield {
        NoteUpdateLog(0, noteId.toInt, action, Timestamp.valueOf(changed), Some(newPassword), Some(oldPassword))
      }
  }
}