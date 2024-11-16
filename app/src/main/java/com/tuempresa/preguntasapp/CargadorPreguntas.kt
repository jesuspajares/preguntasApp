package com.tuempresa.preguntasapp

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

class CargadorPreguntas(private val inputStream: InputStream) {
    fun cargarPreguntas(): List<Pregunta> {
        val preguntas = mutableListOf<Pregunta>()
        try {
            val workbook = XSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex)
                val preguntadorCell = row.getCell(0)
                val preguntaCell = row.getCell(1)
                val respuestaCell = row.getCell(2)

                if (preguntadorCell.cellType == CellType.STRING &&
                    preguntaCell.cellType == CellType.STRING &&
                    respuestaCell.cellType == CellType.STRING
                ) {
                    preguntas.add(
                        Pregunta(
                            preguntadorCell.stringCellValue,
                            preguntaCell.stringCellValue,
                            respuestaCell.stringCellValue
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return preguntas
    }
}
