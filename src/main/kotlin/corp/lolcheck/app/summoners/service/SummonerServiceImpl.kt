package corp.lolcheck.app.summoners.service

import corp.lolcheck.app.summoners.repository.SummonerRepository
import corp.lolcheck.app.summoners.service.interfaces.SummonerService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SummonerServiceImpl(summonerRepository: SummonerRepository) : SummonerService
{
}