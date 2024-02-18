package repository

import model.Note

class NoteRepository extends DbContext {

  import ctx._

  def createAndGetId(note: Note): Option[Int] = {
    run(
      query[Note]
        .insertValue(lift(note))
        .returning(_.id)
    ).headOption
  }

  def getAll: List[Note] = {
    run(
      query[Note]
    )
  }

  def getById(id: Int): Option[Note] = {
    run(query[Note].filter(_.id.contains(lift(id)))).headOption
  }

  def findByName(name: String): List[Note] = {
    run(query[Note].filter(_.name like lift(name)))
  }

  def delete(id: Int): Unit = {
    run(query[Note].filter(_.id.contains(lift(id))).delete)
  }

  def updatePasswordById(id: Int, newPassword: String): Unit = {
    run(query[Note].filter(_.id.contains(lift(id))).update(_.password -> lift(newPassword)))
  }
}