package service

import model.{Note, NoteUpdateLog}
import repository.{NoteDAO, NoteUpdateLogDAO}
import util.CSVHandler

import java.io.File
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NoteService @Inject()(
                             notesRepository: NoteDAO,
                             updateHistoryLogRepository: NoteUpdateLogDAO
                           ) {

  private val csvHandler = new CSVHandler()

  def getNotes(name: String): Future[Seq[Note]] = {
    notesRepository.findAllByName(name)
  }

  def createNote(note: Note): Future[Int] = {
    notesRepository.create(note).flatMap { noteId =>
      updateHistoryLogRepository.create(
        NoteUpdateLog(0, noteId, "CREATE", getTimeStamp(), Some(note.password), None)
      )
    }
  }

  def updateNotePassword(id: Int, newPassword: String): Future[Unit] = {
    notesRepository.getById(id).map { maybeFoundNote =>
      maybeFoundNote.foreach { foundNote =>
        notesRepository.updatePasswordById(id, newPassword).flatMap { _ =>
          updateHistoryLogRepository.create(
            NoteUpdateLog(0, id, "UPDATE", getTimeStamp(), Some(newPassword), Some(foundNote.password))
          )
        }
      }
    }
  }

  def deleteNote(id: Int): Future[Unit] = {
    notesRepository.getById(id).map { maybeFoundNote =>
      maybeFoundNote.foreach { foundNote =>
        notesRepository.delete(id).flatMap { _ =>
          updateHistoryLogRepository.create(
            NoteUpdateLog(0, id, "DELETE", getTimeStamp(), None, Some(foundNote.password))
          )
        }
      }
    }
  }

  def exportToSCV(): Future[File] = {
    val newFile = File.createTempFile("notes", ".csv")
    val notes = notesRepository.getAll.map { notesSeq =>
      notesSeq.map { note =>
        List(note.name, note.password, note.lastChanged.toString)
      }.toList
    }
    csvHandler.writeData(newFile, notes)
  }

  def importFromCSV(inputFile: File): Future[Unit] = {
    val data = csvHandler.getData(inputFile)
    Future {
      data.map { line =>
        try {
          notesRepository.create(makeNoteFromLine(line))
        } catch {
          case _: Exception =>
          // я обязательно всё обработаю
        }
      }
    }
  }

  private def makeNoteFromLine(line: List[String]): Note = {
    if (line.size != 3) throw new Exception("Многовато у вас столбцов")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    Note(
      name = line.head,
      password = line(1),
      lastChanged = getTimeStamp(Some(LocalDateTime.parse(line(2), formatter)))
    )
  }

  private def getTimeStamp(dateTime: Option[LocalDateTime] = None): Timestamp = {
    if (dateTime.isDefined) Timestamp.valueOf(dateTime.get) else Timestamp.valueOf(LocalDateTime.now)
  }

}
