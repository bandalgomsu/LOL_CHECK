package corp.lolcheck.app.device.service

import corp.lolcheck.app.device.DeviceTestConstant.DEVICE_ID
import corp.lolcheck.app.device.DeviceTestConstant.DEVICE_TOKEN
import corp.lolcheck.app.device.DeviceTestConstant.UPDATE_DEVICE_TOKEN
import corp.lolcheck.app.device.DeviceTestConstant.USER_ID
import corp.lolcheck.app.device.domain.Device
import corp.lolcheck.app.device.dto.DeviceRequest
import corp.lolcheck.app.device.dto.DeviceResponse
import corp.lolcheck.app.device.repsitory.DeviceRepository
import corp.lolcheck.app.summoners.exception.DeviceErrorCode
import corp.lolcheck.common.exception.BusinessException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockitoAnnotations
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class DeviceServiceImplTest() {
    var deviceRepository: DeviceRepository = mockk<DeviceRepository>()

    var deviceService: DeviceServiceImpl = spyk(DeviceServiceImpl(deviceRepository))

    var device: Device = Device(
        id = DEVICE_ID,
        deviceToken = DEVICE_TOKEN,
        userId = USER_ID
    )

    @BeforeEach
    fun beforeEach() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("디바이스_생성_성공_테스트")
    fun 디바이스_생성_성공_테스트() = runTest {
        val request = DeviceRequest.DeviceCreateRequest(
            deviceToken = DEVICE_TOKEN
        )

        coEvery { deviceRepository.save(any()) } returns device

        val response: DeviceResponse.DeviceInfo = deviceService.createDevice(USER_ID, request)

        Assertions.assertEquals(DEVICE_ID, response.deviceId)
        Assertions.assertEquals(DEVICE_TOKEN, response.deviceToken)
        Assertions.assertEquals(USER_ID, response.userId)
    }

    @Test
    @DisplayName("유저아이디를_통한_디바이스_정보_조회")
    fun 유저아이디를_통한_디바이스_정보_조회() = runTest {
        coEvery { deviceRepository.findAllByUserId(USER_ID) } returns flow { emit(device) }

        val responseList: List<DeviceResponse.DeviceInfo> = deviceService.getDevicesInfoByUserId(USER_ID)
        val response: DeviceResponse.DeviceInfo = responseList.get(0)

        Assertions.assertEquals(1, responseList.size)
        Assertions.assertEquals(DEVICE_ID, response.deviceId)
        Assertions.assertEquals(DEVICE_TOKEN, response.deviceToken)
        Assertions.assertEquals(USER_ID, response.userId)
    }

    @Test
    @DisplayName("유저아이디_리스트를_통한_디바이스_토큰_리스트_조회")
    fun 저아이디_리스트를_통한_디바이스_토큰_리스트_조회() = runTest {
        val userIds: MutableList<Long> = mutableListOf(USER_ID)
        coEvery { deviceRepository.findAllByUserIdIn(userIds) } returns flow { emit(device) }

        val deviceTokens: List<String> = deviceService.getDeviceTokensByUserIds(userIds)

        Assertions.assertEquals(1, deviceTokens.size)
        Assertions.assertEquals(DEVICE_TOKEN, deviceTokens[0])
    }

    @Test
    @DisplayName("디바이스_수정_성공_테스트")
    fun 디바이스_수정_성공_테스트() = runTest {
        val request = DeviceRequest.DeviceUpdateRequest(
            deviceToken = UPDATE_DEVICE_TOKEN
        )

        device.deviceToken = UPDATE_DEVICE_TOKEN

        coEvery { deviceRepository.findByIdAndUserId(DEVICE_ID, USER_ID) } returns device

        val response = deviceService.updateDevice(USER_ID, DEVICE_ID, request)

        Assertions.assertEquals(UPDATE_DEVICE_TOKEN, response.deviceToken)
        Assertions.assertEquals(DEVICE_ID, response.deviceId)
        Assertions.assertNotEquals(DEVICE_TOKEN, response.deviceToken)
        Assertions.assertEquals(USER_ID, response.userId)
    }

    @Test
    @DisplayName("디바이스_수정_실패_테스트_존재하지_않는_디바이스")
    fun 디바이스_수정_실패_테스트_존재하지_않는_디바이스() = runTest {
        val request = DeviceRequest.DeviceUpdateRequest(
            deviceToken = UPDATE_DEVICE_TOKEN
        )

        coEvery {
            deviceRepository.findByIdAndUserId(
                DEVICE_ID,
                USER_ID
            )
        } throws BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)
        assertFailsWith<BusinessException> { deviceService.updateDevice(USER_ID, DEVICE_ID, request) }
    }

    @Test
    @DisplayName("디바이스_삭제_성공_테스트")
    fun 디바이스_삭제_성공_테스트() = runTest {
        coEvery { deviceRepository.findByIdAndUserId(DEVICE_ID, USER_ID) } returns device
        coEvery { deviceRepository.delete(device) } returns Unit

        deviceService.deleteDevice(USER_ID, DEVICE_ID)
        coVerify(exactly = 1) { deviceRepository.delete(device) }
    }

    @Test
    @DisplayName("디바이스_삭제_실패_테스트_존재하지_않는_디바이스")
    fun 디바이스_삭제_실패_테스트_존재하지_않는_디바이스() = runTest {
        coEvery {
            deviceRepository.findByIdAndUserId(
                DEVICE_ID,
                USER_ID
            )
        } throws BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND)

        assertFailsWith<BusinessException> { deviceService.deleteDevice(USER_ID, DEVICE_ID) }
    }
}