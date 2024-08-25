package ru.forbidden.sequence.testproject

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.sql.SQLException
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(basePackageClasses = [CashCardRepository::class])
@EntityScan(basePackageClasses = [CashCard::class])
class FlywayTestConfiguration {
//    @Bean
//    @Throws(SQLException::class)
//    fun dataSource(): DataSource {
//        return PreparedDbProvider
//            .forPreparer(FlywayPreparer.forClasspathLocation("db/migrations"))
//            .createDataSource()
//    }


}