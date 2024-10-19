package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SummonerServiceImpl(
    private var summonerRepository: SummonerRepository,
    private var riotClient: RiotClient,
) : SummonerService {
    
    override suspend fun registrySummoner(gameName: String, tagLine: String): SummonerResponse.SummonerInfo {
        var puuid: String = this.getSummonerPuuid(gameName, tagLine)

        val summoner = Summoner(
            puuid = puuid,
            gameName = gameName,
            tagLine = tagLine,
        )

        val save: Summoner = summonerRepository.save(summoner).awaitSingle()

        return SummonerResponse.SummonerInfo(
            summonerId = save.id,
            puuid = save.puuid,
            gameName = save.gameName,
            tagLine = save.tagLine,
            recentGame = save.recentGame
        )
    }

    override suspend fun getSummonerPuuid(gameName: String, tagLine: String): String {
        return riotClient.getPuuid(gameName, tagLine).puuid
    }

    override suspend fun checkPlayingGame(puuid: String): Boolean {
        return riotClient.getCurrentGameInfo(puuid)
    }
}