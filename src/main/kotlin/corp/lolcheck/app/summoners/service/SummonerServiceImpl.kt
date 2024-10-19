package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import corp.lolcheck.infrastructure.riot.RiotClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SummonerServiceImpl(
    private var summonerRepository: SummonerRepository,
    private var riotClient: RiotClient,
) : SummonerService {

}