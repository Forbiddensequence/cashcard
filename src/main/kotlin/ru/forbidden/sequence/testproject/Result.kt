package ru.forbidden.sequence.testproject

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import ru.forbidden.sequence.testproject.Result.Companion.GenericStatus.ERROR
import ru.forbidden.sequence.testproject.Result.Companion.GenericStatus.OK

@JsonInclude(NON_NULL)
data class Result<R>(
    val meta: Meta,
    val data: R? = null,
) {
    @JsonInclude(NON_NULL)
    data class Meta(
        val status: GenericStatus?,
        val message: String? = null,
        val description: String? = null,
    )

    companion object {
        fun <R> ok(data: R?) = Result(Meta(OK), data)

        fun <R> of(
            meta: Meta,
            data: R? = null,
        ) = Result(meta, data)

        fun <R> of(
            status: GenericStatus,
            data: R? = null,
        ) = Result(Meta(status), data)

        fun <R> error(data: R? = null) = Result(Meta(ERROR), data)

        enum class GenericStatus {
            OK,
            CREATED,
            ERROR,
        }
    }
}
