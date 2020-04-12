package com.github.xmaiax

import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class InGameProtocol {

  companion object {

    private val logger = LoggerFactory.getLogger(InGameProtocol::class.java)

    fun loop(socketChannel: SocketChannel) {
      val buffer = ByteBuffer.allocate(Packet.MAX_SIZE)
      val size = socketChannel.read(buffer)
      if(size > 0) {
        buffer.position(0)
        val packetSize = Packet.readInt16(buffer)
        val rawType = Packet.readByte(buffer)
        logger.debug("New in-game packet [Size=$packetSize, Type=0x${"%02x".format(rawType)}]")
      }
    }

  }

}
