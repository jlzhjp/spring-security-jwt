package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts

import java.util.*
import javax.crypto.spec.SecretKeySpec

class JwtService {
    companion object {
        private const val SECRET_KEY = "2cd7a0a076513654c9ab9c8d2c34c83d9557e3817e790e36e6c698e8a0ef858f0d7a8d178fc53db8f00287a4185c2cf481db8660de47d8b0bd3e8e1571934d0af4007182ada739cf1860530817758b311e3657e3fa27c7f2b54fd59035a74d1c52fbf0a5617bd0446a1d104a42cd3f765758ff3abb424fabaae45ea52653cdd2c80cae411a4d3ab1d3578230ea67be1d699df1e90fff2cc3afe89a19e4a3dc048e8ecb7f6bb46f4270cca00a19e7bdbb5138f665dc54111f49449ce0e424069f37be645fad7642c23874d32e1d499a56158b2b4849c4a3b7ba0e9b9c0fe200db0e3a03c145f44c1f983429c85a73da45d7d79251fadcf5fb711a383a527dbac022c0bbfe30bc20b0501e7d8e99ae766ce9e676142ac1b022fe8a52285071de80beb5603af6e6e8120c1b970ca826a1be4289b0d40770996819e445e269b162a7f38d29400b914e688c68f6fd3677cbc5e44c9280465aed13c72ff0118020b968afbcdb68350bae89d667a4d1cbdc1f60377491cedd53c5a27a43dd577eb33232cdf13f7ef11a46bdf1df53a0a100607c7b95dbf87cdb88df76223a705f0e9deab073baf3414b19e5236c63dd59f216f551fb9fb623ec1a9849a135773fe26c1faba81b9dd5cbf272140b2b9e1e7462396233b538ebf78c4f24fdb44fd507a9e4e361fb7414770019e31643668b26f2c4ceb4309d576c88a849a4bc219ee96f86"
        private val SigningKey = SecretKeySpec(Base64.getDecoder().decode(SECRET_KEY), "HmacSHA256")
    }

    fun generateToken(
        user: User,
        extraClaims : Map<String, Any> = emptyMap()
    ): String {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(user.id.toString())
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
            .signWith(SigningKey)
            .compact()
    }

    fun parseToken(jwt: String): Jws<Claims> {
        val parser = Jwts.parser().verifyWith(SigningKey).build()
        return parser.parseSignedClaims(jwt)
    }
}