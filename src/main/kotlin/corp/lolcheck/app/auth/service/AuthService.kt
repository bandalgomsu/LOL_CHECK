package corp.lolcheck.app.auth.service

import JwtToken
import corp.lolcheck.app.auth.dto.AuthRequest
import corp.lolcheck.app.auth.dto.AuthResponse
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.repository.UserRepository
import kotlinx.coroutines.coroutineScope
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    suspend fun signUp(request: AuthRequest.SignUpRequest): AuthResponse.TokenResponse = coroutineScope {
        val user: User = User(email = request.email, password = passwordEncoder.encode(request.password))

        val savedUser: User = userRepository.save(user)

        val accessToken: JwtToken = jwtService.generateAccessToken(savedUser.email)
        val refreshToken: JwtToken = jwtService.generateRefreshToken()

        AuthResponse.TokenResponse(
            accessToken = accessToken.value,
            refreshToken = refreshToken.value
        )
    }
}