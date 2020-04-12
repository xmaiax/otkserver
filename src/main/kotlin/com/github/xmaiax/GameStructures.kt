package com.github.xmaiax

enum class Direction(val code: Byte) {
  NORTH(0x00), EAST(0x01),
  SOUTH(0x02), WEST(0x03),
  NORTHEAST(0x04), NORTHWEST(0x05),
  SOUTHEAST(0x06), SOUTHWEST(0x07);

  companion object {
    fun fromCode(codigo: Byte) =
      Direction.values().filter {
        it.code == codigo
      }.first()
  }
}

data class Position(val x: Int, val y: Int, val z: Byte) {
  fun move(direction: Direction): Position = when (direction) {
    Direction.EAST  -> this.copy(x = x + 1)
    Direction.NORTH -> this.copy(y = y - 1)
    Direction.SOUTH -> this.copy(y = y + 1)
    Direction.WEST  -> this.copy(x = x - 1)
    Direction.NORTHWEST ->
      this.move(Direction.NORTH)
          .move(Direction.EAST)
    Direction.NORTHEAST ->
      this.move(Direction.NORTH)
          .move(Direction.WEST)
    Direction.SOUTHEAST ->
      this.move(Direction.SOUTH)
          .move(Direction.EAST)
    Direction.SOUTHWEST ->
      this.move(Direction.SOUTH)
          .move(Direction.WEST)
  }
}

data class Light(
  val radius: Byte = 0xff.toByte(),
  val color: Byte = 0xd7.toByte()
)

enum class SkillType {
  MAGIC, FIST, CLUB, SWORD,
  AXE, DISTANCE, SHIELD, FISHING
}

data class Skill(val type: SkillType,
  var level: Byte = 0, var percent: Byte = 0)

enum class MessageType(val code: Byte) {
  YELLOW(0x01), LIGHT_BLUE(0x04),
  EVENT(0x14), ORANGE(0x11), WARNING(0x02),
  INFO(0x16), IN_PROGRESS_EVENT(0x13),
  BLUE(0x18), RED(0x19), STATUS(0x15),
  DISCREET_STATUS(0x17);

  companion object {
    fun fromCode(code: Byte) =
      MessageType.values().filter {
        it.code == code
      }.first()
  }
}

enum class Channel(
  val _name: String, val code: Int,
  val isPublic: Boolean, val isSpecial: Boolean
) {
  CLAN("Clan", 0x00, false, false),
  REPORT("Report", 0x03, false, true),
  CHAT("Chat", 0x04, true, false),
  TRADE("Trade", 0x05, true, false),
  RL_CHAT("RL Chat", 0x06, true, false),
  HELP("Help", 0x07, true, false),
  DEVS("Maintainer", 0x08, false, true),
  TUTOR("Tutor", 0x09, false, true),
  GM("GM", 0x10, false, true),
  PRIVATE("Private", 0xff, false, false)
}

enum class Skull(val code: Byte) {
  NONE(0x00), WHITE(0x01),
  YELLOW(0x02), RED(0x03);

  companion object {
    fun fromCode(code: Byte) =
      Skull.values().filter {
        it.code == code
      }.first()
  }
}

enum class StatusShield(val code: Byte) {
  NONE(0x00)
}

enum class Slot(val code: Byte) {
  HEAD(0x01), NECK(0x02), BACKPACK(0x03),
  CHEST(0x04), RIGHT_HAND(0x05),
  LEFT_HAND(0x06), LEGS(0x07), FEET(0x08),
  RING(0x09), EXTRA(0x0a), LAST(0x0b);

  companion object {
    fun fromCode(code: Byte) =
      Slot.values().filter {
        it.code == code
      }.first()
  }
}

enum class ChatType(val code: Byte) {
  NORMAL(0x01), WHISPER(0x02), YELL(0x03),
  YELLOW_CHAT(0x05), REPORT_CHANNEL(0x06),
  REPORT_RESPONSE(0x07), REPORT(0x08),
  BROADCAST(0x09), PRIVATE(0x04), RED_CHANNEL(0x0a),
  PRIVATE_RED(0x0b), ORANGE_CHANNEL(0x0c),
  ANONYMOUS_RED(0x0d), MONSTER(0x10),
  SCREAMING_MONSTER(0x11);

  companion object {
    fun fromCode(code: Byte) =
      ChatType.values().filter {
        it.code == code
      }.first()
  }
}

enum class FX(val code: Byte) {
  SPAWN(0x04)
}

enum class TileType(val code: Byte) {
  GRASS(0x6a)
}

data class Tile(
  val type: TileType,
  var entities: MutableList<String> = mutableListOf()
)

enum class Action(val code: Int) {
  LOGOFF(0x14),
  KEEP_CONECTED(0x1e),
  AUTOWALK_ON(0x64),
  MOVE_NORTH(0x65),
  MOVE_EAST(0x66),
  MOVE_SOUTH(0x67),
  MOVE_WEST(0x68),
  AUTOWALK_OFF(0x69),
  MOVE_NORTHEAST(0x6a),
  MOVE_SOUTHEAST(0x6b),
  MOVE_SOUTHWEST(0x6c),
  MOVE_NORTHWEST(0x6d),
  TURN_NORTH(0x6f),
  TURN_EAST(0x70),
  TURN_SOUTH(0x71),
  TURN_WEST(0x72),
  MOVE_ITEM(0x78),
  TRADE_REQUEST(0x7d),
  LOOK_TRADE_ITEM(0x7e),
  ACCEPT_TRADE(0x7f),
  CANCEL_TRADE(0x80),
  USE_ITEM(0x82),
  USE_ITEM_2(0x83),
  BATTLE_WINDOW(0x84),
  TURN_ITEM(0x85),
  CLOSE_CONTAINER(0x87),
  BACK_CONTAINER_BROWSER(0x88),
  TEXT_WINDOW(0x89),
  HOUSE_WINDOW(0x8a),
  LOOK(0x8c),
  TALK(0x96),
  REQUEST_CHANNEL(0x97),
  OPEN_CHANNEL(0x98),
  CLOSE_CHANNEL(0x99),
  OPEN_PRIVATE_CHANNEL(0x9a),
  BATTLE_MODE(0xa0),
  ATTACK(0xa1),
  FOLLOW(0xa2),
  GROUP_INVITE(0xa3),
  GROUP_INVITE_ACCEPT(0xa4),
  REMOVE_INVITE_GROUP(0xa5),
  PASS_GROUP_LEADERSHIP(0xa6),
  LEAVE_GROUP(0xa7),
  CREATE_PRIVATE_CHANNEL(0xaa),
  CHANNEL_INVITE(0xab),
  DELETE_CHANNEL(0xac),
  MOVEMENT_CANCEL(0xbe),
  CLIENT_REQUEST_TILES_RESEND(0xc9),
  CLIENT_REQUEST_CONTAINER_RESEND(0xca),
  OUTFIT_SCREEN(0xd2),
  APPLY_OUTFIT(0xd3),
  ADD_FRIEND(0xdc),
  REMOVE_FRIEND(0xdd);

  companion object {
    fun fromCode(code: Int) =
      Action.values().filter {
        it.code == code
      }.first()
  }

  override fun toString() = name
}

enum class Profession(
  val code: Byte, val _name: String,
  val lifeBase: Int, val lifeLevel: Int,
  val manaBase: Int, val manaLevel: Int,
  val capBase: Int, val capLevel: Int,
  val soulBase: Int
) {
  NECROMANCER(0x01, "Necromancer", 100, 10, 50, 20, 300, 10, 150),
  WARRIOR(    0x02, "Warrior",     200, 25, 20, 5,  450, 15, 100),
  MONK(       0x03, "Monk",        175, 20, 25, 10, 400, 10, 125),
  MAGE(       0X04, "Mage",        125, 5 , 75, 25, 250, 5,  150);

  companion object {
    fun fromCode(code: Byte) =
      Profession.values().filter {
        it.code == code
      }.first()
  }

  override fun toString() = this._name
}

class ExpUtils {
  companion object {
    private fun expQuantity(level: Int) = (Math.pow(level.toDouble(), 1.5) * 100).toLong()
    fun levelFromExp(exp: Long) = Math.cbrt(
      Math.pow(
        java.math.BigDecimal.valueOf(exp).toDouble() / 100.0, 2.0
      )
    ).toInt()
    fun nextLevelPerc(exp: Long): Byte {
      val levelAtual = levelFromExp(exp)
      val expBaseLevelAtual = expQuantity(levelAtual)
      return (((exp - expBaseLevelAtual) * 100) /
        (expQuantity(levelAtual + 1) - expBaseLevelAtual)).toByte()
    }
  }
}

data class Outfit(
  var type: Int, var head: Byte, var body: Byte,
  var legs: Byte, var feet: Byte, var extra: Byte
)

data class Player(
  val identifier: Long,
  val name: String,
  val level: Int,
  var exp: Long,
  var percentNextLevel: Byte,
  val profession: Profession,
  var life: Int,
  val maxLife: Int,
  var mana: Int,
  val maxMana: Int,
  var cap: Int,
  val maxCap: Int,
  var soul: Int,
  var position: Position,
  var direction: Direction,
  val outfit: Outfit,
  var magic: Skill = Skill(SkillType.MAGIC),
  var fist: Skill = Skill(SkillType.FIST),
  var club: Skill = Skill(SkillType.CLUB),
  var sword: Skill = Skill(SkillType.SWORD),
  var axe: Skill = Skill(SkillType.AXE),
  var distance: Skill = Skill(SkillType.DISTANCE),
  var shield: Skill = Skill(SkillType.SHIELD),
  var fishing: Skill = Skill(SkillType.FISHING)
)
