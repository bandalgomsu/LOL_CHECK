package corp.lolcheck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LolCheckApplication

fun main(args: Array<String>) {
    runApplication<LolCheckApplication>(*args)
}
