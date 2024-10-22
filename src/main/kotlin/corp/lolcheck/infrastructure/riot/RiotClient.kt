package corp.lolcheck.infrastructure.riot

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component

class RiotClient(
    @Value("\${riot.api.key}") private val apiKey: String,
) {
    private val logger: Logger? = LoggerFactory.getLogger(RiotClient::class.java)

    suspend fun getPuuid(gameName: String, tagLine: String): RiotClientData.PuuidGetResponse = coroutineScope {
        val uri: String = String.format(
            "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
            gameName, tagLine, apiKey
        )

        WebClient.builder().build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono<RiotClientData.PuuidGetResponse>()
            .awaitSingle()
    }

    suspend fun checkCurrentGameInfo(puuid: String): RiotClientData.CurrentGameResponse = coroutineScope {
        val uri: String = String.format(
            "https://kr.api.riotgames.com/lol/spectator/v5/active-games/by-summoner/%s?api_key=%s", puuid, apiKey
        )

        WebClient.builder().build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono<RiotClientData.CurrentGameResponse>()
            .onErrorResume() {
                Mono.just(
                    RiotClientData.CurrentGameResponse(
                        isCurrentPlayingGame = false
                    )
                )
            }
            .awaitSingle()
    }
}