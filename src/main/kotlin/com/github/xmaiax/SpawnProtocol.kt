package com.github.xmaiax

import org.slf4j.LoggerFactory

class SpawnProtocol {

  companion object {
    val logger = LoggerFactory.getLogger(SpawnProtocol::class.java)
    val PLAYER_IDENTIFIER = 0x0fffffff
    val CLIENT_RENDER_CODE = 0x32
    val ERROR_REPORT_FLAG = 0x00
    val CODE_MAP_INFO = 0x64
    val CODE_INVENTORY_SLOT_FILLED = 0x78
    val CODE_INVENTORY_SLOT_EMPTY = 0x79
    val CODE_STATS = 0xa0
    val CODE_SKILLS = 0xa1
    val CODE_SPAWN_EFFECT = 0x83
    val CODE_AMBIENT_LIGHT = 0x82

    fun createSpawnPacket(characterIdentifier: Long) = Packet()
      .writeByte(Packet.PROCESSING_LOGIN_CODE_OK)
      .writeInt32(PLAYER_IDENTIFIER + characterIdentifier)
      .writeInt16(CLIENT_RENDER_CODE).writeByte(ERROR_REPORT_FLAG)

    fun writePosition(packet: Packet, position: Position) = packet
      .writeInt16(position.x).writeInt16(position.y)
      .writeByte(position.z)

    fun writeLight(packet: Packet, light: Light) = packet
      .writeByte(light.radius).writeByte(light.color)

    fun writeAmbientLight(packet: Packet, light: Light): Packet {
      packet.writeByte(CODE_AMBIENT_LIGHT)
      return writeLight(packet, light)
    }

    fun writeSkill(packet: Packet, skill: Skill) = packet
      .writeByte(skill.level).writeByte(skill.percent)

    fun writeInventorySlot(packet: Packet, slot: Slot) =
      packet.writeByte(CODE_INVENTORY_SLOT_EMPTY).writeByte(slot.code)

    fun writeInventory(packet: Packet): Packet {
      this.writeInventorySlot(packet, Slot.HEAD)
      this.writeInventorySlot(packet, Slot.NECK)
      this.writeInventorySlot(packet, Slot.BACKPACK)
      this.writeInventorySlot(packet, Slot.CHEST)
      this.writeInventorySlot(packet, Slot.RIGHT_HAND)
      this.writeInventorySlot(packet, Slot.LEFT_HAND)
      this.writeInventorySlot(packet, Slot.LEGS)
      this.writeInventorySlot(packet, Slot.FEET)
      this.writeInventorySlot(packet, Slot.RING)
      return this.writeInventorySlot(packet, Slot.EXTRA)
    }

    fun writeStats(packet: Packet, player: Player) {
      packet.writeByte(CODE_STATS)
      packet.writeInt16(player.life)
      packet.writeInt16(player.maxLife)
      packet.writeInt16(player.cap)
      packet.writeInt32(player.exp)
      packet.writeInt16(player.level)
      packet.writeByte(player.percentNextLevel)
      packet.writeInt16(player.mana)
      packet.writeInt16(player.maxMana)
      packet.writeByte(player.magic.level)
      packet.writeByte(player.magic.percent)
      packet.writeByte(player.soul)
    }

    fun writeSkills(packet: Packet, player: Player): Packet {
      fun writeSkill(skill: Skill) =
        packet.writeByte(skill.level).writeByte(skill.percent)
      packet.writeByte(CODE_SKILLS)
      writeSkill(player.fist)
      writeSkill(player.club)
      writeSkill(player.sword)
      writeSkill(player.axe)
      writeSkill(player.distance)
      writeSkill(player.shield)
      return writeSkill(player.fishing)
    }

    fun writeSpawnEffect(packet: Packet,
        position: Position, fx: FX): Packet {
      packet.writeByte(CODE_SPAWN_EFFECT)
      return this.writePosition(packet, position)
        .writeByte(fx.code)
    }

    fun writeMapInfo(packet: Packet, player: Player) {
      packet.writeByte(CODE_MAP_INFO)
      this.writePosition(packet, player.position)
      for(i in 0..251) {
        packet.writeByte(106)
        packet.writeByte(0) // Ground objects
        if(i == 118) { // Player position
          packet.writeByte(97)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(16)
          packet.writeString(player.name)
          packet.writeByte(player.life * 100 / player.maxLife)
          packet.writeByte(2)
          packet.writeByte(128)
          packet.writeByte(10)
          packet.writeByte(20)
          packet.writeByte(30)
          packet.writeByte(40)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
          packet.writeByte(0)
        }
        else packet.writeByte(0)
        packet.writeByte(0xff)
      }
      packet.writeByte(106)
      packet.writeByte(0)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(0xff)
      packet.writeByte(228)
      packet.writeByte(0xff)
    }

    fun successfulLoginPacket(player: Player): Packet {
      logger.info("Attemp to enter in-game: $player")
      val packet = this.createSpawnPacket(player.identifier)
      this.writeMapInfo(packet, player)
      this.writeInventory(packet)
      this.writeStats(packet, player)
      this.writeSkills(packet, player)
      this.writeSpawnEffect(packet, player.position, FX.SPAWN)
      return this.writeAmbientLight(packet, Light())
    }

  }

}
