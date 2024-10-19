package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.domain.Summoner
import corp.lolcheck.app.summoners.dto.SummonerResponse
import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SummonerServiceImpl(
    private val summonerRepository: SummonerRepository,
    private val riotClient: RiotClient,
) : SummonerService {

    @Transactional
    override suspend fun registrySummoner(gameName: String, tagLine: String): SummonerResponse.SummonerInfo =
        coroutineScope {
            val puuid: String = riotClient.getPuuid(gameName, tagLine).awaitSingle().puuid

            val summoner: Summoner = Summoner(
                puuid = puuid,
                gameName = gameName,
                tagLine = tagLine
            )

            val save = summonerRepository.save(summoner)

            SummonerResponse.SummonerInfo(
                summonerId = save.id,
                puuid = save.puuid,
                gameName = save.gameName,
                tagLine = save.tagLine
            )
        }

    @Transactional
    override suspend fun getSummonerInfoByGameNameAndTagLine(
        gameName: String,
        tagLine: String
    ): SummonerResponse.SummonerInfo = coroutineScope {
        var summoner: Summoner

        try {
            summoner = summonerRepository.findByGameNameAndTagLine(gameName, tagLine) ?: throw Exception()
        } catch (e: Exception) {
            val puuid: String = riotClient.getPuuid(gameName, tagLine).awaitSingle().puuid

            summoner = summonerRepository.save(
                Summoner(
                    puuid = puuid,
                    gameName = gameName,
                    tagLine = tagLine
                )
            )
        }

        SummonerResponse.SummonerInfo(
            summonerId = summoner.id,
            puuid = summoner.puuid,
            gameName = summoner.gameName,
            tagLine = summoner.tagLine
        )
    }
}