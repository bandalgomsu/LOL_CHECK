package corp.lolcheck.app.auth.service

import JwtToken
import UserErrorCode
import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.repository.UserRepository
import corp.lolcheck.app.users.type.Role
import corp.lolcheck.common.exception.BusinessException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    suspend fun signUp(request: AuthRequest.SignUpRequest): AuthResponse.TokenResponse = coroutineScope {
        val user: User =
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                role = Role.USER
            )

        val savedUser: User = userRepository.save(user)

        createToken(user.email)
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
        jwtService.validate(token).awaitSingle()

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