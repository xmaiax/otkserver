package com.github.xmaiax

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "CHARACTER_")
data class DatabaseCharacter(

  @Column(name = "NAME_", nullable = false, unique = true)
  var name: String,

  @Column(name = "EXP", nullable = false)
  var exp: Long = 270,

  @Column(name = "PROFESSION_CODE", nullable = false)
  var professionCode: Byte = Profession.NECROMANCER.code,

  @Column(name = "LIFE", nullable = false)
  var life: Int = Profession.NECROMANCER.lifeBase,

  @Column(name = "MANA", nullable = false)
  var mana: Int = Profession.NECROMANCER.manaBase,

  @Column(name = "POS_X", nullable = false)
  var posx: Int = GameMap.RESPAWN_POSITION.x,

  @Column(name = "POS_Y", nullable = false)
  var posy: Int = GameMap.RESPAWN_POSITION.y,

  @Column(name = "POS_Z", nullable = false)
  var posz: Byte = GameMap.RESPAWN_POSITION.z,

  @Column(name = "DIRECTION", nullable = false)
  var direction: Byte = Direction.SOUTH.code,

  @Column(name = "OUTFIT_TYPE", nullable = false)
  var outfitType: Int = 128,

  @Column(name = "OUTFIT_DETAILS_1", nullable = false)
  var outfitDetails1: Byte = 10,

  @Column(name = "OUTFIT_DETAILS_2", nullable = false)
  var outfitDetails2: Byte = 20,

  @Column(name = "OUTFIT_DETAILS_3", nullable = false)
  var outfitDetails3: Byte = 30,

  @Column(name = "OUTFIT_DETAILS_4", nullable = false)
  var outfitDetails4: Byte = 40,

  @Column(name = "OUTFIT_DETAILS_EXTRA", nullable = false)
  var outfitDetailsExtra: Byte = Slot.LAST.code,

  @Column(name = "SKULL_TYPE", nullable = false)
  var skullType: Byte = Skull.NONE.code,

  @Column(name = "MAGIC_LEVEL", nullable = false)
  var magicLevel: Byte = 0,

  @Column(name = "MAGIC_PERCENT", nullable = false)
  var magicPercent: Byte = 0,

  @Column(name = "FIST_LEVEL", nullable = false)
  var fistLevel: Byte = 10,

  @Column(name = "FIST_PERCENT", nullable = false)
  var fistPercent: Byte = 0,

  @Column(name = "CLUB_LEVEL", nullable = false)
  var clubLevel: Byte = 10,

  @Column(name = "CLUB_PERCENT", nullable = false)
  var clubPercent: Byte = 0,

  @Column(name = "SWORD_LEVEL", nullable = false)
  var swordLevel: Byte = 10,

  @Column(name = "SWORD_PERCENT", nullable = false)
  var swordPercent: Byte = 0,

  @Column(name = "AXE_LEVEL", nullable = false)
  var axeLevel: Byte = 10,

  @Column(name = "AXE_PERCENT", nullable = false)
  var axePercent: Byte = 0,

  @Column(name = "DISTANCE_LEVEL", nullable = false)
  var distanceLevel: Byte = 10,

  @Column(name = "DISTANCE_PERCENT", nullable = false)
  var distancePercent: Byte = 0,

  @Column(name = "SHIELD_LEVEL", nullable = false)
  var shieldLevel: Byte = 10,

  @Column(name = "SHIELD_PERCENT", nullable = false)
  var shieldPercent: Byte = 0,

  @Column(name = "FISHING_LEVEL", nullable = false)
  var fishingLevel: Byte = 0,

  @Column(name = "FISHING_PERCENT", nullable = false)
  var fishingPercent: Byte = 0,

  @Id @GeneratedValue
  @Column(name = "ID")
  val id: Long? = null

) : ActiveRecord {

  fun toPlayer() =
    this.id?.let {
      val levelTemp = ExpUtils.levelFromExp(this.exp)
      val professionTemp = Profession.fromCode(this.professionCode)
      Player(
        identifier = it, name = this.name, level = levelTemp, exp = this.exp,
        percentNextLevel = 0, profession = professionTemp,
        life = this.life, maxLife = professionTemp.lifeBase + (levelTemp * professionTemp.lifeLevel),
        mana = this.mana, maxMana = professionTemp.manaBase + (levelTemp * professionTemp.manaLevel),
        cap = professionTemp.capBase,
        maxCap = professionTemp.capBase + (levelTemp * professionTemp.capLevel),
        soul = professionTemp.soulBase,
        position = Position(this.posx, this.posy, this.posz),
        direction = Direction.fromCode(this.direction),
        outfit = Outfit(this.outfitType, this.outfitDetails1,
          this.outfitDetails2, this.outfitDetails3, this.outfitDetails4, this.outfitDetailsExtra),
        magic = Skill(SkillType.MAGIC, this.magicLevel, this.magicPercent),
        fist = Skill(SkillType.FIST, this.fistLevel, this.fistPercent),
        club = Skill(SkillType.CLUB, this.clubLevel, this.clubPercent),
        sword = Skill(SkillType.SWORD, this.swordLevel, this.swordPercent),
        axe = Skill(SkillType.AXE, this.axeLevel, this.axePercent),
        distance = Skill(SkillType.DISTANCE, this.distanceLevel, this.distancePercent),
        shield = Skill(SkillType.SHIELD, this.shieldLevel, this.shieldPercent),
        fishing = Skill(SkillType.FISHING, this.fishingLevel, this.fishingPercent)
      )
    }

}
