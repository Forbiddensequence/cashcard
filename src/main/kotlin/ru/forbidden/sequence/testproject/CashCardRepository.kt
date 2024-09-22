package ru.forbidden.sequence.testproject

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CashCardRepository : JpaRepository<CashCard?, Int?> {
    fun findByIdAndOwner(
        id: Int?,
        owner: String?,
    ): CashCard?

    fun findByOwner(
        owner: String?,
        pageable: Pageable?,
    ): Page<CashCard?>?

    @Query("select cashcard from CashCard cashcard where cashcard.amount >= ?1")
    @Suppress("unused")
    fun findMoreThan(value: Double = 1.0)

}
