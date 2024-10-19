package corp.lolcheck.infrastructure.riot

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
class RiotClient(
    @Value("\${riot.api.key}") private var apiKey: String,
    @Value("\${riot.api.url}") private var baseUrl: String,
) {
    fun getPuuid(gameName: String, tagLine: String): Mono<RiotClientData.GetPuuidResponse> {
        var uri: String = String().format(
            "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s?api_key=%s",
            gameName, tagLine, this.apiKey
        )

        return WebClient.builder().build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono<RiotClientData.GetPuuidResponse>()
    }

    fun getCurrentGameInfo(puuid: String): Mono<Any> {
        var uri: String = String().format(
            "https://kr.api.riotgames.com/lol/spectator/v5/active-games/by-summoner/%s?api_key=%s", puuid, this.apiKey
        )

        return WebClient.builder().build()
            .get()
            .uri(uri)
            .retrieve()
            .bodyToMono<Any>()
    }
}