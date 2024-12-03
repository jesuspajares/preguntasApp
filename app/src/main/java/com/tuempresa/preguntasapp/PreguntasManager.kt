package com.tuempresa.preguntasapp

import android.content.Context
import android.util.Log
import org.apache.poi.ss.usermodel.WorkbookFactory

class PreguntasManager(private val context: Context) {
    var archivoPreguntas = "preguntas.xlsx"

    fun leerPreguntasDesdeAssets(): MutableList<Triple<String, String, String>> {
        val preguntasList = mutableListOf<Triple<String, String, String>>()

        try {
            val assetManager = context.assets
            val inputStream = assetManager.open(archivoPreguntas)

            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 1 until sheet.lastRowNum + 1) {
                val row = sheet.getRow(rowIndex) ?: continue
                val preguntador = row.getCell(0)?.stringCellValue?.trim() ?: ""
                val pregunta = row.getCell(1)?.stringCellValue?.trim() ?: ""
                val respuesta = row.getCell(2)?.stringCellValue?.trim() ?: ""

                // Solo añadir si 'pregunta' y 'respuesta' no están vacías
                if (pregunta.isNotBlank() && respuesta.isNotBlank()) {
                    preguntasList.add(
                        Triple(
                            if (preguntador.isNotBlank()) preguntador else "Desconocido",
                            pregunta,
                            respuesta
                        )
                    )
                }

            }
            workbook.close()
        } catch (e: Exception) {
            Log.e("PreguntasManager", "Error al leer el archivo Excel: ${e.message}")
        }
        preguntasList.shuffle()

        return preguntasList
    }
}
