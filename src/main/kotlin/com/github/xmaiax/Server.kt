package com.github.xmaiax

import java.nio.channels.SocketChannel
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.nio.channels.ServerSocketChannel
import java.net.InetSocketAddress
import java.io.IOException
import java.nio.channels.SelectionKey
import org.springframework.stereotype.Component
import java.nio.channels.Selector
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import java.nio.ByteBuffer

@Component
open class Server(
  @Value("\${otserver.host}") val host: String,
  @Value("\${otserver.port}") val port: Int,
  @Value("\${otserver.version}") val version: Int,
  @Value("\${otserver.motd}") val motd: String,
  var isRunning: Boolean = true
) {

  companion object {
    val logger = LoggerFactory.getLogger(Server::class.java)
  }

  val selector = Selector.open()

  fun newConnectedClient(socketChannel: SocketChannel, key: SelectionKey) {
    val buffer = ByteBuffer.allocate(Packet.MAX_SIZE)
    socketChannel.read(buffer)
    buffer.position(0)
    val packetSize = Packet.readInt16(buffer)
    if(packetSize < 1) return
    val rawType = Packet.readByte(buffer)
    var player: Player? = if(key.attachment() != null) key.attachment() as Player else null
    logger.debug("New received packet [Size=$packetSize, Type=0x${"%02x".format(rawType)}] from ${
      player?.let { "character ${it.name}" } ?: run { "not logged player" }}.")
    player?.let {
      InGameProtocol.loop(socketChannel, it, buffer, Action.fromCode(rawType))
    } ?: run {
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
            DatabaseCharacter(id = 1, name = name).toPlayer()?.let { loggedPlayer ->
              key.attach(loggedPlayer)
              SpawnProtocol.successfulLoginPacket(loggedPlayer).send(socketChannel)
            } ?: run {
              //TODO: Return error message
            }
          }
        }
        else -> logger.debug("Invalid packet received in login...")
      }
    }
  }

  init {
    logger.info("Starting TCP server...")
    val serverSocketChannel = ServerSocketChannel.open()
    serverSocketChannel.configureBlocking(false)
    serverSocketChannel.socket().bind(InetSocketAddress(this.port))
    serverSocketChannel.register(this.selector, serverSocketChannel.validOps())
    Thread({
      while (this.isRunning) {
        this.selector.select()
        val selectedKeysIterator = this.selector.selectedKeys().iterator()
        while (selectedKeysIterator.hasNext()) {
          val key = selectedKeysIterator.next()
          selectedKeysIterator.remove()
          try {
            if (key.isAcceptable())
              serverSocketChannel.accept()?.let { socketChannel ->
                socketChannel.configureBlocking(false)
                socketChannel.register(this.selector,
                  SelectionKey.OP_READ or SelectionKey.OP_WRITE)
              }
            else if (key.isReadable()) {
              val socketChannel = key.channel() as SocketChannel
              try{ this.newConnectedClient(socketChannel, key) }
              catch(e: IOException) { socketChannel.close() }
            }
          }
          catch(e: Exception) {
            logger.error("Error on handling packets: ${e.message}", e)
          }
        }
      }
    }).start()
  }

  @PostConstruct
  fun initOk() {
    logger.info("TCP server started! [port=${this.port}]")
  }

  @PreDestroy
  fun shutdown() {
    this.isRunning = false
    logger.info("TCP server stopping...")
  }

}
