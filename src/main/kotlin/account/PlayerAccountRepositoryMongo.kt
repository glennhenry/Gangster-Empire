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

    override suspend fun getAccountByUsername(username: String): Result<PlayerAccount> {
        val filters = Filters.eq("username", username)

        return runMongoCatching {
            accounts.find(filters).firstOrNull()
                ?: throw NoSuchElementException("Account with username=$username doesn't exist")
        }
    }

    override suspend fun getAccountByPlayerId(playerId: Int): Result<PlayerAccount> {
        val filters = Filters.eq("playerId", playerId)

        return runMongoCatching {
            accounts.find(filters).firstOrNull()
                ?: throw NoSuchElementException("Account with playerId=$playerId doesn't exist")
        }
    }

    override suspend fun getPlayerIdByUsername(username: String): Result<Int> {
        val filters = Filters.eq("profile.displayName", username)
        val projections = Projections.include("playerId")

        return runMongoCatching {
            accounts
                .find(filters)
                .projection(projections)
                .firstOrNull()
                ?.playerId
                ?: throw NoSuchElementException("Account with username=$username doesn't exist")
        }
    }

    override suspend fun updatePlayerAccount(
        playerId: Int,
        account: PlayerAccount
    ): Result<Unit> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)

            val result = accounts.replaceOne(filters, account)
            if (result.matchedCount < 1) {
                throw NoSuchElementException("PlayerAccount of playerId=$playerId wasn't updated because it wasn't found")
            }
            if (result.modifiedCount < 1) {
                throw NoSuchElementException("Fail to update PlayerAccount of playerId=$playerId")
            }
        }
    }

    override suspend fun updateLastLogin(playerId: Int, lastLogin: Long): Result<Unit> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)
            val updates = Updates.set("lastLogin", lastLogin)

            val result = accounts.updateOne(filters, updates)
            if (result.matchedCount < 1) {
                throw NoSuchElementException("Last login of playerId=$playerId wasn't updated because it wasn't found")
            }
            if (result.modifiedCount < 1) {
                throw NoSuchElementException("Fail to update last login of playerId=$playerId")
            }
        }
    }

    /**
     * Verify credentials
     *
     * @throws NoSuchElementException if username isn't found.
     * @throws IllegalArgumentException if password does not match.
     */
    override suspend fun verifyCredentials(
        username: String,
        password: String
    ): Result<Int> {
        return runMongoCatching {
            val filters = Filters.eq("username", username)
            val projection = Projections.include("hashedPassword", "playerId")

            val acc = accounts
                .withDocumentClass<Document>()
                .find(filters)
                .projection(projection)
                .firstOrNull()
                ?: throw NoSuchElementException("Account with username=$username isn't found")

            val hashed = acc.getString("hashedPassword")
            val playerId = acc.getInteger("playerId")
            val matches = Bcrypt.verify(password, Base64.decode(hashed))

            if (matches) playerId else throw IllegalArgumentException("Wrong password for username=$username")
        }
    }
}
