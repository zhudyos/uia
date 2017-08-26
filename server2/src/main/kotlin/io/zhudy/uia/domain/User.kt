package io.zhudy.uia.domain

import com.fasterxml.jackson.annotation.JsonFilter

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@JsonFilter("filterOutAllExcept")
data class User(
        val id: Long,
        val email: String = "",
        val password: String = "",
        val createdTime: Long
)