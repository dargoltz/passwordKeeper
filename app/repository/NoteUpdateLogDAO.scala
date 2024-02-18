package repository

import model.NoteUpdateLog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NoteUpdateLogDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val logs = lifted.TableQuery[NoteUpdateLogsTable]


  def getAll: Future[Seq[NoteUpdateLog]] = {
    db.run(logs.result).map(NoteUpdateLogConverter.makeFromSeq)
  }

  def findByNoteId(noteId: Int): Future[Seq[NoteUpdateLog]] = {
    db.run(logs.filter(_.noteId === noteId).result).map(NoteUpdateLogConverter.makeFromSeq)
  }

  def create(log: NoteUpdateLog): Future[Int] = {
    db.run(logs += (0, log.noteId, log.action, log.changed, log.newPassword, log.oldPassword))
  }

  private class NoteUpdateLogsTable(tag: Tag) extends Table[(Int, Int, String, Timestamp, Option[String], Option[String])](tag, "note_update_log") {
    def id = column[Int]("id", O.PrimaryKey) // This is the primary key column

    def noteId = column[Int]("note_id")

    def action = column[String]("action")

    def changed = column[Timestamp]("changed")

    def newPassword = column[Option[String]]("new_password")

    def oldPassword = column[Option[String]]("old_password")

    def * = (id, noteId, action, changed, newPassword, oldPassword)
  }

  private object NoteUpdateLogConverter {
    def makeFromSeq(rows: Seq[(Int, Int, String, Timestamp, Option[String], Option[String])]): Seq[NoteUpdateLog] = {
      rows.map {
        case (id, noteId, action, changed, newPassword, oldPassword) =>
          NoteUpdateLog(Option(id), noteId, action, changed, newPassword, oldPassword)
      }
    }
  }
}
