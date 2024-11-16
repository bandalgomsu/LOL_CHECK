package corp.lolcheck.app.auth.service

import AuthErrorCode
import JwtToken
import UserErrorCode
import corp.lolcheck.app.auth.data.AuthRedisKey
import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.repository.UserRepository
import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.exception.BusinessException
import corp.lolcheck.common.util.MailValidator
import corp.lolcheck.infrastructure.redis.RedisClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val redisClient: RedisClient,
) {
    @Transactional
    suspend fun signUp(request: AuthRequest.SignUpRequest): AuthResponse.TokenResponse = coroutineScope {
        listOf(
            async { MailValidator.validateEmail(email = request.email) },
            async { checkIsDuplicatedEmail(email = request.email) },
            async { checkIsVerifiedUser(email = request.email) }
        ).awaitAll()

        val user: User =
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = Role.USER
            )

        val savedUser: User = userRepository.save(user)

        createToken(savedUser.email)
    }

    private suspend fun checkIsVerifiedUser(email: String) = coroutineScope {
        redisClient.getData(AuthRedisKey.IS_VERIFIED_USER.combineKeyValue(email), String::class)
            ?: throw BusinessException(AuthErrorCode.NOT_VERIFIED_USER)
    }

    private suspend fun checkIsDuplicatedEmail(email: String) = coroutineScope {
        if (userRepository.findByEmail(email) != null) {
            throw BusinessException(AuthErrorCode.DUPLICATE_EMAIL)
        }
    }

    suspend fun login(request: AuthRequest.LoginRequest): AuthResponse.TokenResponse = coroutineScope {
        val user: User =
            userRepository.findByEmail(request.email) ?: throw BusinessException(UserErrorCode.USER_NOT_FOUND)

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(UserErrorCode.INVALID_PASSWORD)
        }

        createToken(user.email)
    }

    suspend fun refresh(request: AuthRequest.RefreshRequest): AuthResponse.TokenResponse = coroutineScope {
        val token: JwtToken = JwtToken(request.refreshToken)

        jwtService.validate(token).awaitSingleOrNull()

        createToken(jwtService.getEmail(token).awaitSingle())
    }

    suspend private fun createToken(email: String): AuthResponse.TokenResponse = coroutineScope {
        val accessToken: JwtToken = jwtService.generateAccessToken(email).awaitSingle()
        val refreshToken: JwtToken = jwtService.generateRefreshToken(email).awaitSingle()

        AuthResponse.TokenResponse(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value
        )
    }
}