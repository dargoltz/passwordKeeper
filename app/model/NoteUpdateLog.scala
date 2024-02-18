package model

import play.api.libs.json.{Format, JsResult, JsValue, Json}

import java.sql.Timestamp

case class NoteUpdateLog(id: Int, noteId: Int, action: String, changed: Timestamp, newPassword: Option[String], oldPassword: Option[String])

object NoteUpdateLog {
  implicit val format: Format[NoteUpdateLog] = Json.format[NoteUpdateLog]

  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = Json.toJson(t.getTime)
    def reads(json: JsValue): JsResult[Timestamp] = json.validate[Long].map(new Timestamp(_))
  }
}