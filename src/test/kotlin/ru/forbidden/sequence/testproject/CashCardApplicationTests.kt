package ru.forbidden.sequence.testproject

import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest( webEnvironment = RANDOM_PORT)
@Testcontainers
@Sql("/db/migration/V1.0.0TEST__test_data.sql")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun shouldReturnACashCardWhenDataIsSaved() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(response.body)
        val id = documentContext.read<Int>("$.data.id")
        val amount = documentContext.read<Double>("$.data.amount")
        assertThat(id).isEqualTo(99)
        assertThat(amount).isEqualTo(123.45)
    }

    @Test
    fun shouldNotReturnACashCardWithAnUnknownId() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards/-1000", String::class.java)
        assertThat(response.statusCode).isEqualTo(NOT_FOUND)
        val documentContext = JsonPath.parse(response.body)
        val status = documentContext.read<String>("$.meta.status")
        assertThat(status).isEqualTo("ERROR")
    }

    @Test
    @DirtiesContext
    fun shouldCreateANewCashCard() {
        val newCashCard = CashCard(amount = 250.00)
        val createResponse = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .postForEntity("/api/cashcards", newCashCard, String::class.java)
        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.CREATED)

        // Add assertions such as these
        val createdDocumentContext = JsonPath.parse(createResponse.body)
        assertThat(createdDocumentContext.read<Number>("$.meta.status")).isEqualTo("CREATED")

        val locationOfNewCashCard = createResponse.headers.getLocation()
        val getResponse = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity(locationOfNewCashCard, String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(OK)

        // Add assertions such as these
        val getDocumentContext = JsonPath.parse(getResponse.body)
        val id = getDocumentContext.read<Number>("$.data.id")
        val amount = getDocumentContext.read<Double>("$.data.amount")
        assertThat(id).isNotNull()
        assertThat(amount).isEqualTo(250.00)
    }

    @Test
    fun shouldReturnAllCashCardsWhenListIsRequested() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards", String::class.java)
        assertThat(response.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(response.body)
        val cashCardCount = documentContext.read<Int>("$.data.length()")
        assertThat(cashCardCount).isEqualTo(3)
        val ids: JSONArray = documentContext.read("$.data..id")
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101)
        val amounts: JSONArray = documentContext.read("$.data..amount")
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00)
    }

    @Test
    fun shouldReturnAPageOfCashCards() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards?page=0&size=1", String::class.java)
        assertThat(response.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(response.body)
        val page = documentContext.read<JSONArray>("$.data[*]")
        assertThat(page.size).isEqualTo(1)
    }

    @Test
    fun shouldReturnASortedPageOfCashCards() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards?page=0&size=1&sort=amount,desc", String::class.java)
        assertThat(response.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(response.body)
        val read = documentContext.read<JSONArray>("$.data[*]")
        assertThat(read.size).isEqualTo(1)
        val amount = documentContext.read<Double>("$.data[0].amount")
        assertThat(amount).isEqualTo(150.00)
    }

    @Test
    fun shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards?sort=amount,asc", String::class.java)
        assertThat(response.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(response.body)
        val page = documentContext.read<JSONArray>("$.data[*]")
        assertThat(page.size).isEqualTo(3)
        val amounts = documentContext.read<JSONArray>("$.data..amount")
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00)
    }

    @Test
    fun shouldNotReturnACashCardWhenUsingBadCredentials() {
        var response = restTemplate!!
            .withBasicAuth("BAD-USER", "abc123")
            .getForEntity("/api/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(UNAUTHORIZED)
        response = restTemplate!!
            .withBasicAuth("sarah1", "BAD-PASSWORD")
            .getForEntity("/api/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(UNAUTHORIZED)
    }

    @Test
    fun shouldRejectUsersWhoAreNotCardOwners() {
        val response = restTemplate!!
            .withBasicAuth("hank", "qrs456")
            .getForEntity("/api/cashcards/99", String::class.java)
        assertThat(response.statusCode).isEqualTo(FORBIDDEN)
    }

    @Test
    fun shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards/102", String::class.java) // kumar2's data
        assertThat(response.statusCode).isEqualTo(NOT_FOUND)
    }

    @Test
    @DirtiesContext
    fun shouldUpdateAnExistingCashCard() {
        val cashCardUpdate = CashCard(amount = 19.99)
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .exchange("/api/cashcards/99", HttpMethod.PUT, HttpEntity(cashCardUpdate), Void::class.java)
        assertThat(response.statusCode).isEqualTo(NO_CONTENT)
        val getResponse = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .getForEntity("/api/cashcards/99", String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(OK)
        val documentContext = JsonPath.parse(getResponse.body)
        val id = documentContext.read<Number>("$.data.id")
        val amount = documentContext.read<Double>("$.data.amount")
        assertThat(id).isEqualTo(99)
        assertThat(amount).isEqualTo(19.99)
    }

    @Test
    fun shouldNotUpdateACashCardThatDoesNotExist() {
        val unknownCard = CashCard(null, 19.99, null)
        val request = HttpEntity(unknownCard)
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .exchange("/api/cashcards/99999", HttpMethod.PUT, request, Void::class.java)
        assertThat(response.statusCode).isEqualTo(NOT_FOUND)
    }

    @Test
    @DirtiesContext
    fun shouldDeleteAnExistingCashCard() {
        val response = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .exchange("/api/cashcards/99", DELETE, null, Void::class.java)
        assertThat(response.statusCode).isEqualTo(NO_CONTENT)
    }

    @Test
    fun shouldNotDeleteACashCardThatDoesNotExist() {
        val deleteResponse = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .exchange("/api/cashcards/99999", DELETE, null, Void::class.java)
        assertThat(deleteResponse.statusCode).isEqualTo(NOT_FOUND)
    }

    @Test
    fun shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        val deleteResponse = restTemplate!!
            .withBasicAuth("sarah1", "abc123")
            .exchange("/api/cashcards/102", DELETE, null, Void::class.java)
        assertThat(deleteResponse.statusCode).isEqualTo(NOT_FOUND)

        val getResponse = restTemplate!!
            .withBasicAuth("kumar2", "xyz789")
            .getForEntity("/api/cashcards/102", String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(OK)
    }



    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @BeforeAll
        fun setUp() {
            postgresContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerPgProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
        }
    }
}
