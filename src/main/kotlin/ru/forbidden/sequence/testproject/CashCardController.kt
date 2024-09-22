package ru.forbidden.sequence.testproject

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import java.security.Principal
import ru.forbidden.sequence.testproject.Result.Companion.GenericStatus.CREATED as META_CREATED

@RestController
@RequestMapping("/api/cashcards")
class CashCardController (
    private val cashCardRepository: CashCardRepository,
) {
    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: Int,
        principal: Principal,
    ): ResponseEntity<Result<CashCard?>> =
        cashCardRepository.findByIdAndOwner(id = id, owner = principal.name).let { cashCard ->
            if (cashCard != null) {
                ResponseEntity.ok(Result.ok(cashCard))
            } else {
                ResponseEntity.status(NOT_FOUND).body(Result.error())
            }
        }

    @GetMapping
    fun findAll(
        pageable: Pageable,
        principal: Principal,
    ): ResponseEntity<Result<List<CashCard>>> =
        (cashCardRepository.findByOwner(owner = principal.name, pageable = pageable) as Page<CashCard>).let {
            ResponseEntity
                .status(OK)
                .header(X_PAGE_SIZE, it.pageable.pageSize.toString())
                .header(X_PAGE_NUMBER, it.pageable.pageNumber.toString())
                .header(X_TOTAL, it.totalElements.toString())
                .body(Result.ok(it.content))
        }

    @PostMapping
    private fun createCashCard(
        @RequestBody request: CashCard,
        principal: Principal,
        ucb: UriComponentsBuilder,
    ): ResponseEntity<Result<Void>> =
        cashCardRepository.save(CashCard(amount = request.amount, owner = principal.name)).let { card ->
            ResponseEntity
                .status(CREATED)
                .header(
                    LOCATION,
                    ucb
                        .path("/api/cashcards/{id}")
                        .build(card.id)
                        .toASCIIString(),
                ).body(Result.of(META_CREATED))
        }

    @PutMapping("/{id}")
    private fun putCashCard(
        @PathVariable id: Int,
        @RequestBody cashCardUpdate: CashCard,
        principal: Principal,
    ): ResponseEntity<Result<Void>> =
        cashCardRepository.findByIdAndOwner(id, principal.name)?.let { cashCard ->
            cashCardRepository.save(
                CashCard(
                    id = cashCard.id,
                    amount = cashCardUpdate.amount,
                    owner = principal.name,
                ),
            )
            ResponseEntity.noContent().build()
        } ?: ResponseEntity.status(NOT_FOUND).body(Result.error())

    @DeleteMapping("/{id}")
    private fun deleteCashCard(
        @PathVariable id: Int,
        principal: Principal,
    ): ResponseEntity<Result<Void>> =
        cashCardRepository.findByIdAndOwner(id, principal.name)?.let {
            cashCardRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } ?: ResponseEntity.status(NOT_FOUND).body(Result.error())
}
