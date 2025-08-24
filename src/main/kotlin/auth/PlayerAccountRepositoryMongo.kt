package dev.gangster.auth

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.toxicbakery.bcrypt.Bcrypt
import dev.gangster.data.collection.PlayerAccount
import dev.gangster.db.runMongoCatching
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import kotlin.io.encoding.Base64

class PlayerAccountRepositoryMongo(
    val accounts: MongoCollection<PlayerAccount>
) : PlayerAccountRepository {

    override suspend fun doesUserExist(username: String): Result<Boolean> {
        val filters = Filters.eq("username", username)

        return runMongoCatching {
            accounts
                .find(filters)
                .projection(null)
                .firstOrNull() != null
        }
    }

    override suspend fun getAccountByUsername(username: String): Result<PlayerAccount?> {
        val filters = Filters.eq("username", username)

        return runMongoCatching {
            accounts.find(filters).firstOrNull()
        }
    }

    override suspend fun getAccountByPlayerId(playerId: Long): Result<PlayerAccount?> {
        val filters = Filters.eq("playerId", playerId)

        return runMongoCatching {
            accounts.find(filters).firstOrNull()
        }
    }

    override suspend fun getPlayerIdByUsername(username: String): Result<Long?> {
        val filters = Filters.eq("profile.displayName", username)
        val projections = Projections.include("playerId")

        return runMongoCatching {
            accounts
                .find(filters)
                .projection(projections)
                .firstOrNull()
                ?.playerId
        }
    }

    override suspend fun updatePlayerAccount(
        playerId: Long,
        account: PlayerAccount
    ): Result<Unit> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)

            val result = accounts.replaceOne(filters, account)
            if (result.modifiedCount < 1) {
                throw NoSuchElementException("playerId=$playerId not on updatePlayerAccount")
            }
        }
    }

    override suspend fun updateLastLogin(playerId: Long, lastLogin: Long): Result<Unit> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)
            val updates = Updates.set("lastLogin", lastLogin)

            val result = accounts.updateOne(filters, updates)
            if (result.modifiedCount < 1) {
                throw NoSuchElementException("playerId=$playerId not on updateLastLogin")
            }
        }
    }

    override suspend fun verifyCredentials(
        username: String,
        password: String
    ): Result<Long?> {
        return runMongoCatching {
            val filters = Filters.eq("username", username)
            val projection = Projections.include("hashedPassword", "playerId")

            val acc = accounts
                .withDocumentClass<Document>()
                .find(filters)
                .projection(projection)
                .firstOrNull()

            if (acc == null) return@runMongoCatching null

            val hashed = acc.getString("hashedPassword")
            val playerId = acc.getLong("playerId")
            val matches = Bcrypt.verify(password, Base64.decode(hashed))

            if (matches) playerId else null
        }
    }
}
