package utils

import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.math.abs

class EncodingDetector {
    
    companion object {
        fun detectEncoding(file: File): Charset {
            return when {
                isUTF8WithBOM(file) -> StandardCharsets.UTF_8
                isUTF16LE(file) -> StandardCharsets.UTF_16LE
                isUTF16BE(file) -> StandardCharsets.UTF_16BE
                isWindows1251(file) -> Charset.forName("windows-1251")
                isKOI8R(file) -> Charset.forName("KOI8-R")
                else -> detectByStatistics(file) ?: StandardCharsets.UTF_8
            }
        }
        
        private fun isUTF8WithBOM(file: File): Boolean {
            FileInputStream(file).use { fis ->
                val bytes = ByteArray(3)
                return if (fis.read(bytes) >= 3) {
                    bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte()
                } else {
                    false
                }
            }
        }
        
        private fun isUTF16LE(file: File): Boolean {
            FileInputStream(file).use { fis ->
                val bytes = ByteArray(2)
                return if (fis.read(bytes) >= 2) {
                    bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte()
                } else {
                    false
                }
            }
        }
        
        private fun isUTF16BE(file: File): Boolean {
            FileInputStream(file).use { fis ->
                val bytes = ByteArray(2)
                return if (fis.read(bytes) >= 2) {
                    bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte()
                } else {
                    false
                }
            }
        }
        
        private fun isWindows1251(file: File): Boolean {
            return try {
                FileInputStream(file).use { fis ->
                    val bytes = fis.readAllBytes()
                    String(bytes, Charset.forName("windows-1251"))
                    true
                }
            } catch (e: Exception) {
                false
            }
        }
        
        private fun isKOI8R(file: File): Boolean {
            return try {
                FileInputStream(file).use { fis ->
                    val bytes = fis.readAllBytes()
                    String(bytes, Charset.forName("KOI8-R"))
                    true
                }
            } catch (e: Exception) {
                false
            }
        }
        
        private fun detectByStatistics(file: File): Charset? {
            FileInputStream(file).use { fis ->
                val bytes = fis.readAllBytes()
                
                val candidates = listOf(
                    StandardCharsets.UTF_8,
                    Charset.forName("windows-1251"),
                    Charset.forName("ISO-8859-1"),
                    StandardCharsets.US_ASCII
                )
                
                return candidates.maxByOrNull { charset ->
                    try {
                        val text = String(bytes, charset)
                        calculateReadabilityScore(text)
                    } catch (e: Exception) {
                        0
                    }
                }
            }
        }
        
        private fun calculateReadabilityScore(text: String): Int {
            var score = 0
            for (char in text) {
                when {
                    char.isLetterOrDigit() -> score += 2
                    char.isWhitespace() -> score += 1
                    char in ".,!?;:-()[]{}" -> score += 1
                    char.code in 0..127 -> score += 1
                    else -> {
                        if (char.code in 1040..1103  char == 'ё'  char == 'Ё') {
                            score += 3
                        }
                    }
                }
            }
            return score
        }
    }
}
