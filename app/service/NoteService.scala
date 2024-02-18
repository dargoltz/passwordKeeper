package service

import model.{Note, NoteUpdateLog}
import repository.{NoteRepository, NoteUpdateLogDAO}
import util.CSVHandler

import java.io.File
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NoteService @Inject()(
                             notesRepository: NoteRepository,
                             updateHistoryLogRepository: NoteUpdateLogDAO
                           ) {

  private val csvHandler = new CSVHandler()

  def getNotes(name: String): List[Note] = {
    notesRepository.findByName(name)
  }

  def createNote(note: Note): Unit = {
    notesRepository.createAndGetId(note) match {
      case Some(noteId) =>
        updateHistoryLogRepository.create(NoteUpdateLog(None, noteId, "CREATE", note.lastChanged, Some(note.password), None))
    }
  }

  def updateNotePassword(id: Int, password: String): Unit = {
    notesRepository.getById(id) match {
      case Some(foundNote) =>
        notesRepository.updatePasswordById(id, password)
        updateHistoryLogRepository.create(NoteUpdateLog(None, id, "UPDATE", getTimeStamp(), Some(password), Some(foundNote.password)))
    }
  }

  def deleteNote(id: Int): Unit = {
    notesRepository.getById(id) match {
      case Some(foundNote) =>
        notesRepository.delete(id)
        updateHistoryLogRepository.create(NoteUpdateLog(None, id, "DELETE", getTimeStamp(), None, Some(foundNote.password)))
    }
  }

  def exportToSCV(): File = {
    val newFile = File.createTempFile("export", ".csv")
    csvHandler.writeData(newFile, notesRepository.getAll.map(note => note.toList))
  }

  def importFromCSV(inputFile: File): Future[Unit] = {
    val data = csvHandler.getData(inputFile)
    Future {
      data.map { line =>
        try {
          notesRepository.createAndGetId(makeNoteFromLine(line))
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
      id = None,
      name = line.head,
      password = line(1),
      lastChanged = getTimeStamp(Some(LocalDateTime.parse(line(2), formatter)))
    )
  }

  private def getTimeStamp(dateTime: Option[LocalDateTime] = None): Timestamp = {
    if (dateTime.isDefined) Timestamp.valueOf(dateTime.get) else Timestamp.valueOf(LocalDateTime.now)
  }

}
