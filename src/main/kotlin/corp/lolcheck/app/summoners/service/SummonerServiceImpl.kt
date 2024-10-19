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
    private val summonerRepository: SummonerRepository,
    private val riotClient: RiotClient,
) : SummonerService {
    override suspend fun registrySummoner(gameName: String, tagLine: String): SummonerResponse.SummonerInfo {
        val puuid: String = riotClient.getPuuid(gameName, tagLine).awaitSingle().puuid

        val summoner: Summoner = Summoner(
            puuid = puuid,
            gameName = gameName,
            tagLine = tagLine
        )

        val save: Summoner = summonerRepository.save(summoner).awaitSingle()

        return SummonerResponse.SummonerInfo(
            summonerId = save.id,
            puuid = save.puuid,
            gameName = save.gameName,
            tagLine = save.tagLine
        )
    }

    override suspend fun getSummonerInfoByGameNameAndTagLine(
        gameName: String,
        tagLine: String
    ): SummonerResponse.SummonerInfo {
        val summoner: Summoner = summonerRepository.findByGameNameAndTagLine(gameName, tagLine)
            .awaitSingle()

        return SummonerResponse.SummonerInfo(
            summonerId = summoner.id,
            puuid = summoner.puuid,
            gameName = summoner.gameName,
            tagLine = summoner.tagLine
        )
    }
}