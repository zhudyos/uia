package io.zhudy.uia.helper

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import org.springframework.stereotype.Component

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@Component
class MongoSeqHelper(db: MongoDatabase) {

    /**
     *
     */
    private val counters = db.getCollection("counters")

    /**
     *
     */
    fun next(name: String): Long {
        val doc = counters.findOneAndUpdate(Filters.eq(name), Updates.inc("seq", 1L), FindOneAndUpdateOptions().upsert(true))
        return doc.getLong("seq")
    }
}