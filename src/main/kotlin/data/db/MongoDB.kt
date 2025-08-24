package dev.gangster.db

import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.toxicbakery.bcrypt.Bcrypt
import dev.gangster.data.collection.PlayerAccount
import dev.gangster.data.collection.PlayerCounter
import dev.gangster.data.collection.PlayerData
import dev.gangster.data.collection.model.AvatarData
import dev.gangster.data.collection.model.ServerMetadata
import dev.gangster.data.db.CollectionName
import dev.gangster.game.data.AdminData
import dev.gangster.utils.Logger
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64

/**
 * Implementation of game [Database] using MongoDB.
 *
 * You must install MongoDB Community Edition to use this.
 */
class MongoDB(db: MongoDatabase) : Database {
    private val accounts = db.getCollection<PlayerAccount>("playeraccount")
    private val data = db.getCollection<PlayerData>("playerdata")
    private val counter = db.getCollection<PlayerCounter>("playercounter")

    init {
        Logger.info { "Initializing MongoDB..." }
        CoroutineScope(Dispatchers.IO).launch {
            setupCollections()
        }
    }

    private suspend fun setupCollections() {
        try {
            val count = accounts.estimatedDocumentCount()
            Logger.info { "MongoDB: User collection ready, contains $count users." }
            createAdminAccount()
        } catch (e: Exception) {
            Logger.error { "MongoDB: Failed during setupCollections: $e" }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> getCollection(name: CollectionName): T {
        return when (name) {
            CollectionName.PLAYER_ACCOUNT_COLLECTION -> accounts
            CollectionName.PLAYER_DATA_COLLECTION -> data
            CollectionName.COUNTER_COLLECTION -> counter
        } as T
    }

    private suspend fun nextPlayerId(startAt: Int = 10): Int {
        return runMongoCatching {
            val doc = counter.findOneAndUpdate(
                Filters.eq("_id", "playerCounter"),
                Updates.inc("seq", 1),
                FindOneAndUpdateOptions()
                    .returnDocument(ReturnDocument.AFTER)
                    .upsert(true)
            )

            if (doc == null) {
                counter.insertOne(PlayerCounter(seq = startAt))
                return startAt
            }

            return@runMongoCatching doc.seq
        }.getOrThrow()
    }

    private suspend fun createAdminAccount() {
        runMongoCatching {
            val adminDoc = accounts.find(Filters.eq("playerId", AdminData.PLAYER_ID_NUMBER)).firstOrNull()
            if (adminDoc == null) {
                val start = getTimeMillis()

                val acc = PlayerAccount.admin()
                val dat = PlayerData.admin()

                accounts.insertOne(acc)
                data.insertOne(dat)

                Logger.info { "MongoDB: Admin account inserted in ${getTimeMillis() - start}ms" }
            } else {
                Logger.info { "MongoDB: Admin account already exists." }
            }
        }
    }

    override suspend fun loadPlayerAccount(playerId: Int): Result<PlayerAccount> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)
            accounts.find(filters)
                .firstOrNull()
                ?: throw NoSuchElementException("MongoDB: PlayerAccount for playerId=$playerId not found.")
        }
    }

    override suspend fun loadPlayerData(playerId: Int): Result<PlayerData> {
        return runMongoCatching {
            val filters = Filters.eq("playerId", playerId)
            data.find(filters)
                .firstOrNull()
                ?: throw NoSuchElementException("MongoDB: PlayerData for playerId=$playerId not found.")
        }
    }

    override suspend fun createPlayer(
        username: String,
        email: String,
        password: String,
        avatarData: AvatarData
    ): Result<Int> {
        return runMongoCatching {
            val pid = nextPlayerId()
            val acc = PlayerAccount(
                playerId = pid,
                username = username,
                email = email,
                hashedPassword = hashPw(password),
                createdAt = getTimeMillis(),
                lastLogin = getTimeMillis(),
                serverMetadata = ServerMetadata()
            )

            val dat = PlayerData(
                playerId = pid,
                x = 0
            )

            accounts.insertOne(acc)
            data.insertOne(dat)

            pid
        }
    }

    private fun hashPw(password: String): String {
        return Base64.encode(Bcrypt.hash(password, 10))
    }

    override suspend fun close() {
        // Mongo does not need close
        Logger.info { "MongoDB: database closed." }
    }
}

/**
 * Executes the given [block] that returns a value of type [T] or null.
 *
 * - If [block] returns null, this will return a failed [Result] containing a [NoSuchElementException]
 *   with the provided [nullMessage].
 * - If [block] throws any exception, it will be caught and wrapped in a failed [Result].
 * - Otherwise, the returned non-null value is wrapped in a successful [Result].
 *
 * You need to throw exception explicitly if there are multiple exception messages.
 */
inline fun <T> runMongoCatching(
    nullMessage: String = "Document not found",
    block: () -> T?
): Result<T> {
    return try {
        val value = block()
        if (value == null) {
            Result.failure(NoSuchElementException(nullMessage))
        } else {
            Result.success(value)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Executes the given [block] that returns a [Boolean] indicating success.
 *
 * - If [block] returns false, this will return a failed [Result] containing a [NoSuchElementException]
 *   with the provided [failMessage].
 * - If [block] throws any exception, it will be caught and wrapped in a failed [Result].
 * - If [block] returns true, a successful [Result] with [Unit] is returned.
 *
 * You need to throw exception explicitly if there are multiple exception messages.
 */
inline fun runMongoCatchingUnit(
    failMessage: String = "Document not found",
    block: () -> Boolean
): Result<Unit> {
    return try {
        if (block()) Result.success(Unit)
        else Result.failure(NoSuchElementException(failMessage))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
