package corp.lolcheck.infrastructure

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@EnableR2dbcRepositories
@EnableR2dbcAuditing
@TestConfiguration
class InfraConfig {
}