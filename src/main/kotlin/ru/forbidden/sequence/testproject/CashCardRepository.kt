package ru.forbidden.sequence.testproject

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface CashCardRepository : JpaRepository<CashCard?, Int?> {

    fun findByIdAndOwner(id: Int?, owner: String?): CashCard?
    fun findByOwner(owner: String?, pageable: Pageable?): Page<CashCard?>?
}
