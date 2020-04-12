package com.github.xmaiax

class MD5Utils {
  companion object {
    private val messageDigest =
      java.security.MessageDigest.getInstance("MD5")
    private val SALT = "9b783618935148a5fd" //FIXME Change this magic numbers
    fun str2md5(password: String) =
      java.math.BigInteger(1,
        messageDigest.digest(
          "$password$SALT".toByteArray(
            java.nio.charset.StandardCharsets.UTF_8
          )
        )
      ).toString(16).padStart(32, '0')
  }
}
