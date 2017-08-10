package io.zhudy.uia.helper

import com.mongodb.MongoClient
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import org.bson.Document
import org.springframework.stereotype.Component

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class MongoSeqHelper(mongoClient: MongoClient) {

    val counters = mongoClient.getDatabase("uia").getCollection("user")!!

    fun createSeq(name: String) {
        if (counters.find(eq(name)).first() != null) {
            counters.insertOne(Document(mapOf("_id" to "user_id", "seq" to 0L)))
        }
    }

    fun nextSeq(name: String): Long {
        val doc = counters.findOneAndUpdate(eq(name), Updates.inc("seq", 1L))
        return doc.getLong("seq")
    }
}