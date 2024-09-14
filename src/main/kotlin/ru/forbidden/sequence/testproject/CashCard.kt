package ru.forbidden.sequence.testproject

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "CASH_CARD")
class CashCard(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Int? = null,
    @Column
    var amount: Double? = null,
    @Column
    var owner: String? = null,
) {
    override fun equals(other: Any?): Boolean = (other is CashCard) && id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "CashCard(id=$id, amount=$amount, owner=$owner)"
}
