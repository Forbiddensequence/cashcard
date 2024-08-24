package ru.forbidden.sequence.testproject

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import java.io.IOException


@JsonTest
class CashCardJsonTest {
    @Autowired
    private val json: JacksonTester<CashCard>? = null

    @Autowired
    private val jsonList: JacksonTester<Array<CashCard>>? = null

    private lateinit var cashCards: Array<CashCard>

    @BeforeEach
    fun setUp() {
        cashCards = arrayOf(
            CashCard(99L.toInt(), 123.45,  "sarah1"),
            CashCard(100L.toInt(), 1.00,  "sarah1"),
            CashCard(101L.toInt(), 150.00,  "sarah1")
        )
    }

    @Test
    @Throws(IOException::class)
    fun cashCardSerializationTest() {
        val cashCard: CashCard = cashCards[0]
        assertThat(json!!.write(cashCard)).isStrictlyEqualToJson("/testcash.json")
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id")
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
            .isEqualTo(99)
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount")
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
            .isEqualTo(123.45)
    }

    @Test
    @Throws(IOException::class)
    fun cashCardDeserializationTest() {
        val expected = """
                {
                    "id": 99,
                    "amount": 123.45
                }
                
                """.trimIndent()
        assertThat(json!!.parse(expected))
            .isEqualTo(CashCard(99L.toInt(), 123.45))
        assertThat(json.parseObject(expected).id).isEqualTo(99)
        assertThat(json.parseObject(expected).amount).isEqualTo(123.45)
    }



    @Test
    @Throws(IOException::class)
    fun cashCardListDeserializationTest() {
        val expected = """
                [
                     {"id": 99, "amount": 123.45 , "owner": "sarah1"},
                     {"id": 100, "amount": 1.00 , "owner": "sarah1"},
                     {"id": 101, "amount": 150.00, "owner": "sarah1"}
                                                  
                ]
                
                """.trimIndent()
        assertThat(jsonList!!.parse(expected)).isEqualTo(cashCards)
    }
}

