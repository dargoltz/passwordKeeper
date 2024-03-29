package repository

import model.NoteUpdateLog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.Future

class NoteUpdateLogDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val logs = lifted.TableQuery[NoteUpdateLogsTable]


  def getAll: Future[Seq[NoteUpdateLog]] = {
    db.run(logs.result)
  }

  def findByNoteId(noteId: Int): Future[Seq[NoteUpdateLog]] = {
    db.run(logs.filter(_.noteId === noteId).result)
  }

  def create(log: NoteUpdateLog): Future[Int] = {
    db.run(logs += log)
  }

  private class NoteUpdateLogsTable(tag: Tag) extends Table[NoteUpdateLog](tag, "note_update_log") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def noteId = column[Int]("note_id")
    def action = column[String]("action")
    def changed = column[Timestamp]("changed")
    def newPassword = column[Option[String]]("new_password")
    def oldPassword = column[Option[String]]("old_password")

    def * = (id, noteId, action, changed, newPassword, oldPassword) <> ((NoteUpdateLog.apply _).tupled, NoteUpdateLog.unapply)
  }
}
