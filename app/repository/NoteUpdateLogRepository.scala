package repository

import model.NoteUpdateLog

@Singleton
class NoteUpdateLogRepository extends DbContext {

  import ctx._

  private val noteUpdateLogs = quote(querySchema[NoteUpdateLog]("note_update_log"))

  def getAll: List[NoteUpdateLog] = {
    run(
      query[NoteUpdateLog]
    )
  }

  def findByNoteId(noteId: Int): List[NoteUpdateLog] = {
    run(noteUpdateLogs.filter(_.noteId == lift(noteId)))
  }

  def create(log: NoteUpdateLog): Unit = {
    run(noteUpdateLogs.insertValue(lift(log)))
  }

}
