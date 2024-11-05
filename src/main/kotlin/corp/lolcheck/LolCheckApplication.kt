package corp.lolcheck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@EnableR2dbcAuditing
@SpringBootApplication
class LolCheckApplication

fun main(args: Array<String>) {
    runApplication<LolCheckApplication>(*args)
}
