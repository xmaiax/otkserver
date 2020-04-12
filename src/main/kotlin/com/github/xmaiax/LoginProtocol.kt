package com.github.xmaiax

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.InputStream
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

enum class OperatingSystem(private val type: String) {
  UNIX_LIKE("Unix-like"), WINDOWS("Windows"), OTHER("Other");

  override fun toString() = this.type

  companion object {
    fun fromCode(code: Int) =
      when (code) {
        0x01 -> UNIX_LIKE
        0x02 -> WINDOWS
        else -> OTHER
      }
  }
}

enum class LoginRequestType(private val code: Byte) {
  LOAD_CHARACTER_LIST(0x01),
  LOGIN_SUCCESS(0x0a),
  INVALID(-1);

  override fun toString() = this.name

  companion object {
    fun fromCode(code: Int): LoginRequestType {
      val filter = LoginRequestType.values().filter {
        it.code == code.toByte()
      }
      return if(filter.isNotEmpty())
        filter.first() else INVALID
    }
  }
}

data class MOTD(
  val message: String,
  val code: Byte = 0x01
) {
  override fun toString() = "$code\n$message"
}

data class OTKSLoginException(override val message: String): Exception(message)

fun PacketAccount(buffer: ByteBuffer): PacketAccount {
  Packet.skip(buffer, 12)
  val number = Packet.readInt32(buffer)
  val password = Packet.readString(buffer)
  if(number < 1) throw OTKSLoginException("Please insert the account number.")
  password?.let { return PacketAccount(number, MD5Utils.str2md5(it)) }
  throw OTKSLoginException("Please insert a password.")
}
data class PacketAccount(
  val accountNumber: Int,
  val encryptedPassword: String)

fun LoginProtocol(
  buffer: ByteBuffer,
  worldClientVersion: Int,
  isCharacterSelected: Boolean = false
): LoginProtocol {
  val operatingSystem = OperatingSystem.fromCode(Packet.readInt16(buffer))
  val clientVersion = Packet.readInt16(buffer)
  if(clientVersion != worldClientVersion) throw OTKSLoginException(
    "Expected client $worldClientVersion, got client $clientVersion.")
  if (isCharacterSelected) {
    Packet.skip(buffer, 1)
    val accountNumber = Packet.readInt32(buffer)
    val characterName = Packet.readString(buffer)
    Packet.readString(buffer)?.let { password ->
      return LoginProtocol(
        operatingSystem, clientVersion,
        PacketAccount(accountNumber, MD5Utils.str2md5(password)),
        characterName)
    }
    throw OTKSLoginException("Nice try, a**hole!")
  }
  else
    return LoginProtocol(operatingSystem, clientVersion, PacketAccount(buffer))
}

data class LoginProtocol(
  val operatingSystem: OperatingSystem,
  val version: Int, val account: PacketAccount,
  val selectedCharacterName: String? = null
) {

  companion object {
    val logger = LoggerFactory.getLogger(LoginProtocol::class.java)
  }

  data class CharacterOption(
    val name: String,
    val vocation: String
  )

  fun createLoginPacket(host: String, port: Int, motd: MOTD): Packet {
    logger.debug("Creating login packet from $this")
    val packet = Packet()
    packet.writeByte(Packet.LOGIN_CODE_OK)
    packet.writeString(motd.toString())
    packet.writeByte(Packet.CHARACTERS_LIST_START)
    val premmyDaysLeft = 12
    val characters = arrayOf(
      CharacterOption("Maia", "Necromancer"),
      CharacterOption("Stefane", "Rogue")
    )
    if(characters.size > 0) {
      packet.writeByte(characters.size)
      characters.forEach { character ->
        packet.writeString(character.name)
        packet.writeString(character.vocation)
        java.net.InetAddress.getByName(host)
          .getHostAddress().split(".").forEach {
            packet.writeByte(it.toInt())
          }
        packet.writeInt16(port)
      }
    }
    else packet.writeByte(0)
    packet.writeInt16(premmyDaysLeft)
    return packet
  }

}
