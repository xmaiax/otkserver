package com.github.xmaiax

import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.channels.Selector
import java.nio.channels.SelectionKey

class InGameProtocol {

  companion object {

    private val logger = LoggerFactory.getLogger(InGameProtocol::class.java)

    fun loop(socketChannel: SocketChannel, player: Player,
        buffer: ByteBuffer, action: Action) {
      logger.info("Executing action ${action.name} from character ${player.name}...")
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
            packet.send(socketChannel, true)
          }
        }
        else -> {
          logger.warn("Not implemented action: $action")
        }
      }
    }

  }

}
