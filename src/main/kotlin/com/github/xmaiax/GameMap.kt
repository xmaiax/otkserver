package com.github.xmaiax

class GameMap {

  companion object {
    val RESPAWN_POSITION = Position(50, 50, 7)
    val fieldOfViewX = 8
    val fieldOfViewY = 6
    private var map = HashMap<Position, Tile>()
    fun initialize() {
      for (x in 0..100) for (y in 0..100)
        map.put(
          Position(x, y, 7),
          Tile(TileType.GRASS)
        )
    }

    fun getTile(position: Position) = this.map.get(position)
  }

}
