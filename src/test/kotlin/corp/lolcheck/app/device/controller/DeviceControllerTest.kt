package corp.lolcheck.app.device.controller

import com.ninjasquad.springmockk.MockkBean
import corp.lolcheck.app.TestSecurityConfig
import corp.lolcheck.app.auth.config.SecurityConfig
import corp.lolcheck.app.auth.data.CustomUserDetails
import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse
import corp.lolcheck.app.device.service.interfaces.DeviceService
import corp.lolcheck.app.users.domain.User
import corp.lolcheck.app.users.type.Role
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.context.TestSecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient


@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [DeviceController::class],
    excludeAutoConfiguration = [SecurityConfig::class]
)
@Import(TestSecurityConfig::class)
class DeviceControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockkBean
    private lateinit var deviceService: DeviceService

    @Test
    @WithMockUser
    @DisplayName("CREATE_DEVICE_SUCCESS")
    fun CREATE_DEVICE_SUCCESS() = runTest {
        val userId = 1L
        val deviceCreateRequest = DeviceRequest.DeviceCreateRequest("Device_Name")
        val deviceInfoResponse = DeviceResponse.DeviceInfo(1L, "Device Name", 1L)

        coEvery { deviceService.createDevice(userId, deviceCreateRequest) } returns deviceInfoResponse

        val mockAuthentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(
                User(
                    id = userId,
                    email = "TEST",
                    password = "TEST",
                    role = Role.USER
                )
            ), null, emptyList()
        )

        TestSecurityContextHolder.getContext().authentication = mockAuthentication

        webTestClient
            .post()
            .uri("/api/v1/devices")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(deviceCreateRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(DeviceResponse.DeviceInfo::class.java)
            .isEqualTo(deviceInfoResponse)
    }

    @Test
    @WithMockUser
    @DisplayName("UPDATE_DEVICE_SUCCESS")
    fun UPDATE_DEVICE_SUCCESS() = runTest {
        val userId = 1L
        val deviceId = 1L
        val deviceCreateRequest = DeviceRequest.DeviceUpdateRequest("Device_Name")
        val deviceInfoResponse = DeviceResponse.DeviceInfo(deviceId, "Device Name", userId)

        coEvery { deviceService.updateDevice(userId, deviceId, deviceCreateRequest) } returns deviceInfoResponse

        val mockAuthentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(
                User(
                    id = userId,
                    email = "TEST",
                    password = "TEST",
                    role = Role.USER
                )
            ), null, emptyList()
        )

        TestSecurityContextHolder.getContext().authentication = mockAuthentication

        webTestClient
            .patch()
            .uri("/api/v1/devices/${deviceId}")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(deviceCreateRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(DeviceResponse.DeviceInfo::class.java)
            .isEqualTo(deviceInfoResponse)
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE_ALL_DEVICE_SUCCESS")
    fun DELETE_ALL_DEVICE_SUCCESS() = runTest {
        val userId = 1L

        coEvery { deviceService.deleteAllDevice(userId) } returns Unit

        val mockAuthentication = UsernamePasswordAuthenticationToken(
            CustomUserDetails(
                User(
                    id = userId,
                    email = "TEST",
                    password = "TEST",
                    role = Role.USER
                )
            ), null, emptyList()
        )

        TestSecurityContextHolder.getContext().authentication = mockAuthentication

        webTestClient
            .delete()
            .uri("/api/v1/devices")
            .exchange()
            .expectStatus().isOk
    }
}