package corp.lolcheck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication
class LolCheckApplication

fun main(args: Array<String>) {
    runApplication<LolCheckApplication>(*args)
}
