package test.sls1005.projects.caesarcipherkeyboard

import java.io.File

private const val STORED_DATA_LENGTH = 8

internal data class StoredData(val userDefinedAdditionalOffset: Int, val capitalizationOffset: Int)
// Stored file format:             Line 1, Byte 1-4, L.E.;               Byte 5-8, L.E.

internal fun getStoredData(dir: File): StoredData? {
    val file = File(dir, "config.bin")
    if (! file.exists()) {
        return null
    }
    val a = file.readBytes()
    if (a.size < STORED_DATA_LENGTH) {
        file.delete()
        return null
    }
    return StoredData(
        userDefinedAdditionalOffset = bytesToIntLE(a.slice(0..3).toByteArray()),
        capitalizationOffset = bytesToIntLE(a.slice(4..7).toByteArray())
    )
}

internal fun storeData(dir: File, dataToStore: StoredData) {
    val file = File(dir, "config.bin")
    if (file.exists()) {
        file.delete()
    }
    file.writeBytes(intToBytesLE(dataToStore.userDefinedAdditionalOffset) + intToBytesLE(dataToStore.capitalizationOffset))
}

private fun bytesToIntLE(a: ByteArray): Int {
    var k = 0
    for (i in (a.size - 1) downTo 0) {
        k = k shl 8
        k += a[i].toUByte().toInt()
    }
    return k
}

private fun intToBytesLE(k: Int): ByteArray {
    var x = k
    val a = ByteArray(4)
    for (i in 0 ..< 4) {
        a[i] = x.mod(256).toUByte().toByte()
        x = x shr 8
    }
    return a
}
