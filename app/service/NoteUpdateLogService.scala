package service

import model.NoteUpdateLog
import repository.NoteUpdateLogRepository

import javax.inject.Inject


class NoteUpdateLogService @Inject()(
                                      val noteUpdateLogRepository: NoteUpdateLogRepository
                                    ) {

  def getHistoryLog(noteId: Option[Int] = None): List[NoteUpdateLog] = {
    val foundHistoryLogs = noteId match {
      case Some(id) =>
        noteUpdateLogRepository.findByNoteId(id)
      case None =>
        noteUpdateLogRepository.getAll
    }
    foundHistoryLogs
  }
}
