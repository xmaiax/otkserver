package com.github.xmaiax

import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.channels.Selector
import java.nio.channels.SelectionKey

class InGameProtocol {

  companion object {

    private val logger = LoggerFactory.getLogger(InGameProtocol::class.java)

    fun loop(socketChannel: SocketChannel, selector: Selector, player: Player) {
      socketChannel.register(selector, SelectionKey.OP_READ)
      val buffer = ByteBuffer.allocate(Packet.MAX_SIZE)
      val size = socketChannel.read(buffer)
      if(size > 0) {
        buffer.clear()
        val packetSize = Packet.readInt16(buffer)
        val rawType = Packet.readByte(buffer)
        logger.debug("New in-game packet [Size=$packetSize, Type=0x${"%02x".format(rawType)}]")
        val action = Action.fromCode(rawType)
        when(action) {
          Action.LOGOFF -> {
            logger.debug("Logging off...")
            socketChannel.socket().close()
          }
          Action.TALK -> {
            val chatType = ChatType.fromCode(Packet.readByte(buffer).toByte())
            Packet.readString(buffer)?.let { message ->
              logger.info("${player.name} ($chatType): $message")
              val packet = Packet()
              packet.writeByte(0xaa)
              packet.writeString(player.name)
              packet.writeByte(chatType.code)
              SpawnProtocol.writePosition(packet, player.position)
              packet.writeString(message)
              packet.send(socketChannel, selector)
            }
          }
          else -> {
            logger.warn("Not implemented action: $action")
          }
        }
      }
    }

  }

}
