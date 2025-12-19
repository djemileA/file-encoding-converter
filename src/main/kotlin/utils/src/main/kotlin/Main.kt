import utils.EncodingDetector
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun main() {
    println("=== File Encoding Converter ===")
    
    try {
        print("Введите путь к файлу: ")
        val filePath = readLine()?.trim()
        
        if (filePath.isNullOrEmpty()) {
            println("Ошибка: путь к файлу не может быть пустым")
            return
        }
        
        val inputFile = File(filePath)
        
        if (!inputFile.exists()) {
            println("Ошибка: файл не найден: $filePath")
            return
        }
        
        println("Определение кодировки файла...")
        val detectedCharset = EncodingDetector.detectEncoding(inputFile)
        println("Определенная кодировка: ${detectedCharset.displayName()}")
        
        println("\n=== Содержимое файла ===")
        val content = inputFile.readText(charset = detectedCharset)
        println(content)
        println("=== Конец содержимого ===\n")
        
        val outputFileName = if (inputFile.name.contains(".")) {
            val nameWithoutExt = inputFile.name.substringBeforeLast(".")
            val extension = inputFile.name.substringAfterLast(".")
            "${nameWithoutExt}_win1251.$extension"
        } else {
            "${inputFile.name}_win1251"
        }
        
        val outputFile = File(inputFile.parent, outputFileName)
        
        outputFile.writeText(content, Charset.forName("windows-1251"))
        
        println("Файл сохранен в кодировке Windows-1251: ${outputFile.absolutePath}")
        
    } catch (e: Exception) {
        println("Произошла ошибка: ${e.message}")
    }
}
