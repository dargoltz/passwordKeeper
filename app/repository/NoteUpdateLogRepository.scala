package repository

import model.NoteUpdateLog

class NoteUpdateLogRepository {

  def getAll: List[NoteUpdateLog] = {
    Nil
  }

  def findByNoteId(noteId: Int): List[NoteUpdateLog] = {
    Nil
  }

  def create(log: NoteUpdateLog): Unit = {

  }
}
