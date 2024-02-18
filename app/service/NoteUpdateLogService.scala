package service

import model.NoteUpdateLog
import repository.NoteUpdateLogDAO

import javax.inject.Inject
import scala.concurrent.Future


class NoteUpdateLogService @Inject()(
                                      val noteUpdateLogRepository: NoteUpdateLogDAO
                                    ) {

  def getHistoryLog(noteId: Option[Int] = None): Future[List[NoteUpdateLog]] = {
    val foundHistoryLogs = noteId match {
      case Some(id) =>
        noteUpdateLogRepository.findByNoteId(id)
      case None =>
        noteUpdateLogRepository.getAll
    }
    foundHistoryLogs
  }
}
