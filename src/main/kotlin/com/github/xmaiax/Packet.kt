package com.github.xmaiax

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.channels.Selector
import java.nio.channels.SelectionKey

data class Packet(
  private var size: Int = 0,
  private val buffer: ByteArray = ByteArray(Packet.MAX_SIZE - 2) { 0x00 }
) {
  companion object {
    val MAX_SIZE= 0xffff
    val CHARACTERS_LIST_START = 0x64
    val LOGIN_CODE_OK = 0x14
    val LOGIN_CODE_NOK = 0x0a
    val PROCESSING_LOGIN_CODE_OK = 0x0a
    val PROCESSING_LOGIN_CODE_NOK = 0x14
    fun readByte(input: ByteBuffer)  = input.get().toInt() and 0xff
    fun readInt16(input: ByteBuffer) = readByte(input) or (readByte(input) shl 8)
    fun readInt32(input: ByteBuffer) = readByte(input) or
                                      (readByte(input) shl  8) or
                                      (readByte(input) shl 16) or
                                      (readByte(input) shl 24)

    fun readString(input: ByteBuffer): String? {
      val output = String(ByteArray(Packet.readInt16(input)) {
        Packet.readByte(input).toByte()
      })
      return if (output.isNotEmpty()) output else null
    }

    fun skip(input: ByteBuffer, n: Int) =
      input.position(input.position() + n)
    fun createGenericErrorPacket(
      code: Int, message: String
    ) = Packet().writeByte(code).writeString(message)

  }

  fun writeByte(_byte: Byte): Packet {
    buffer[size++] = _byte
    return this
  }

  fun writeByte(_byte: Int) =
    writeByte((_byte and 0xff).toByte())

  fun writeByte(_byte: Char) =
    writeByte(_byte.toByte())

  fun writeInt16(_int: Int) =
    writeByte(_int and 0x00ff).writeByte((_int and 0xff00) shr 8)

  fun writeInt32(_long: Long) =
    writeByte ((_long and 0x000000ff).toInt()).
    writeByte(((_long and 0x0000ff00) shr 8).toInt()).
    writeByte(((_long and 0x00ff0000) shr 16).toInt()).
    writeByte(((_long and 0xff000000) shr 24).toInt())

  fun writeString(_str: String): Packet {
    writeInt16(_str.length)
    _str.toCharArray().forEach { writeByte(it) }
    return this
  }

  fun bufferWithSize() = byteArrayOf(
    (this.size and 0x00ff).toByte(),
    ((this.size and 0xff00) shr 8).toByte()
  ) + buffer

  fun send(socketChannel: SocketChannel, selector: Selector) {
    val bufferTemp = ByteBuffer.allocate(Packet.MAX_SIZE)
    bufferTemp.put(this.bufferWithSize())
    bufferTemp.flip()
    socketChannel.register(selector, SelectionKey.OP_WRITE)
    while(bufferTemp.hasRemaining())
      socketChannel.write(bufferTemp)
  }

  override fun toString() =
    this.bufferWithSize().map { "0x${"%02x".format(it)}" }
      .toList().subList(0, this.size).toString()

}
