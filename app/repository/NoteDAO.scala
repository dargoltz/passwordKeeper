package repository

import model.Note
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NoteDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                       (implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val notes = TableQuery[NotesTable]

  def create(note: Note): Future[Int] = {
    db.run(notes += note)
  }

  def getAll: Future[Seq[Note]] = {
    db.run(notes.result)
  }

  def getById(id: Int): Future[Option[Note]] = {
    db.run(notes.filter(_.id === id).result.headOption)
  }

  def findAllByName(name: String): Future[Seq[Note]] = {
    db.run(notes.filter(_.name like name).result)
  }

  def delete(id: Int): Future[Int] = {
    db.run(notes.filter(_.id === id).delete)
  }

  def updatePasswordById(id: Int, newPassword: String): Future[Int] = {
    db.run(notes.filter(_.id === id).map(_.password).update(newPassword))
  }

  private class NotesTable(tag: Tag) extends Table[Note](tag, "note") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def password = column[String]("password")

    def lastChanged = column[Timestamp]("last_changed")

    def * = (id.?, name, password, lastChanged) <> ((Note.apply _).tupled, Note.unapply)
  }
}
