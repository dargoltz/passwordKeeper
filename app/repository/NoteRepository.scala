package repository

import model.Note

class NoteRepository() {

  def createAndGetId(note: Note): Option[Int] = {
    None
  }

  def getAll: List[Note] = {
    Nil
  }

  def getById(id: Int): Option[Note] = {
    None
  }

  def findByName(name: String): List[Note] = {
    Nil
  }

  def delete(id: Int): Unit = {

  }

  def updatePasswordById(id: Int, newPassword: String): Unit = {

  }
}