package com.github.xmaiax

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class Server(
  @Value("\${otserver.host}") val host: String,
  @Value("\${otserver.port}") val port: Int,
  @Value("\${otserver.version}") val version: Int,
  @Value("\${otserver.motd}") val motd: String,
  var isRunning: Boolean = true
) {

  companion object {
    val logger = LoggerFactory.getLogger(Server::class.java)
  }

  fun newConnectedClient(socketChannel: SocketChannel) {
    val buffer = ByteBuffer.allocate(Packet.MAX_SIZE)
    socketChannel.read(buffer)
    buffer.position(0)
    val packetSize = Packet.readInt16(buffer)
    val rawType = Packet.readByte(buffer)
    logger.debug("New received packet [Size=$packetSize, Type=0x${"%02x".format(rawType)}]")
    when(LoginRequestType.fromCode(rawType)) {
      LoginRequestType.LOAD_CHARACTER_LIST -> {
        val loginAttemp = LoginProtocol(buffer, this.version, false)
        logger.info("Loading character list: $loginAttemp")
        loginAttemp.createLoginPacket(this.host, this.port,
          MOTD(this.motd)).send(socketChannel)
      }
      LoginRequestType.LOGIN_SUCCESS -> {
        val loginSuccess = LoginProtocol(buffer, this.version, true)
        logger.info("Login success: $loginSuccess")
        loginSuccess.selectedCharacterName?.let { name ->
          DatabaseCharacter(id = 1, name = name).toPlayer()?.let { player ->
            SpawnProtocol.successfulLoginPacket(player).send(socketChannel)
            logger.info("IN GAME!!")
            while(socketChannel.isConnected() && socketChannel.isOpen())
              InGameProtocol.loop(socketChannel)
            logger.info("EXIT?")
          } ?: run {
            //TODO: Return error message
          }
        }
      }
      LoginRequestType.INVALID -> {
        logger.info("MEH!")
      }

    }
  }

  init {
    logger.info("Starting TCP server...")
    val serverSocketChannel = ServerSocketChannel.open()
    serverSocketChannel.configureBlocking(false)
    serverSocketChannel.socket().bind(InetSocketAddress(this.port))
    val selector = Selector.open()
    serverSocketChannel.register(
      selector,
      serverSocketChannel.validOps(), null
    )
    Thread({
      while (this.isRunning) {
        selector.select()
        val selectedKeysIterator = selector.selectedKeys().iterator()
        while (selectedKeysIterator.hasNext()) {
          val key = selectedKeysIterator.next()
          if (key.isAcceptable())
            serverSocketChannel.accept()?.let { socketChannel ->
              socketChannel.configureBlocking(false)
              socketChannel.register(selector, SelectionKey.OP_READ)
            }
          else if (key.isReadable())
            this.newConnectedClient(key.channel() as SocketChannel)
          selectedKeysIterator.remove()
        }
      }
    }).start()
  }

  @PostConstruct
  fun initOk() {
    logger.info("Server started! [port=${this.port}]")
  }

  @PreDestroy
  fun shutdown() {
    this.isRunning = false

  }

}
