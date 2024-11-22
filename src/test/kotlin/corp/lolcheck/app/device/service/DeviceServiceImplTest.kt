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
import kotlin.test.assertEquals
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
    @DisplayName("CREATE_DEVICE_SUCCESS")
    fun CREATE_DEVICE_SUCCESS() = runTest {
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
    @DisplayName("GET_DEVICES_INFO_BY_USER_ID_SUCCESS")
    fun GET_DEVICES_INFO_BY_USER_ID_SUCCESS() = runTest {
        coEvery { deviceRepository.findAllByUserId(USER_ID) } returns flow { emit(device) }

        val responseList: List<DeviceResponse.DeviceInfo> = deviceService.getDevicesInfoByUserId(USER_ID)
        val response: DeviceResponse.DeviceInfo = responseList.get(0)

        Assertions.assertEquals(1, responseList.size)
        Assertions.assertEquals(DEVICE_ID, response.deviceId)
        Assertions.assertEquals(DEVICE_TOKEN, response.deviceToken)
        Assertions.assertEquals(USER_ID, response.userId)
    }

    @Test
    @DisplayName("GET_DEVICE_TOKENS_BY_USER_IDS_SUCCESS")
    fun GET_DEVICE_TOKENS__BY_USER_IDS_SUCCESS() = runTest {
        val userIds: MutableList<Long> = mutableListOf(USER_ID)
        coEvery { deviceRepository.findAllByUserIdIn(userIds) } returns flow { emit(device) }

        val deviceTokens: List<String> = deviceService.getDeviceTokensByUserIds(userIds)

        Assertions.assertEquals(1, deviceTokens.size)
        Assertions.assertEquals(DEVICE_TOKEN, deviceTokens[0])
    }

    @Test
    @DisplayName("UPDATE_DEVICE_SUCCESS")
    fun UPDATE_DEVICE_SUCCESS() = runTest {
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
    @DisplayName("UPDATE_DEVICE_FAILURE_THROW_BY_DEVICE_NOT_FOUND")
    fun UPDATE_DEVICE_FAILURE_THROW_BY_DEVICE_NOT_FOUND() = runTest {
        val request = DeviceRequest.DeviceUpdateRequest(
            deviceToken = UPDATE_DEVICE_TOKEN
        )

        coEvery {
            deviceRepository.findByIdAndUserId(
                DEVICE_ID,
                USER_ID
            )
        } returns null

        val exception = assertFailsWith<BusinessException> { deviceService.updateDevice(USER_ID, DEVICE_ID, request) }

        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("DEVICE_DELETE_SUCCESS")
    fun DEVICE_DELETE_SUCCESS() = runTest {
        coEvery { deviceRepository.findByIdAndUserId(DEVICE_ID, USER_ID) } returns device
        coEvery { deviceRepository.delete(device) } returns Unit

        deviceService.deleteDevice(USER_ID, DEVICE_ID)
        coVerify(exactly = 1) { deviceRepository.delete(device) }
    }

    @Test
    @DisplayName("DEVICE_DELETE_FAILURE_THROW_BY_DEVICE_NOT_FOUND")
    fun DEVICE_DELETE_FAILURE_THROW_BY_DEVICE_NOT_FOUND() = runTest {
        coEvery {
            deviceRepository.findByIdAndUserId(
                DEVICE_ID,
                USER_ID
            )
        } returns null

        val exception = assertFailsWith<BusinessException> { deviceService.deleteDevice(USER_ID, DEVICE_ID) }
        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.code, exception.errorCode.getCodeValue())
        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.message, exception.errorCode.getMessageValue())
        assertEquals(DeviceErrorCode.DEVICE_NOT_FOUND.status, exception.errorCode.getStatusValue())
    }

    @Test
    @DisplayName("DEVICE_DELETE_ALL_SUCCESS")
    fun DEVICE_DELETE_ALL_SUCCESS() = runTest {
        coEvery { deviceRepository.findAllByUserId(USER_ID) } returns flow { emit(device) }
        coEvery { deviceRepository.deleteAll(any()) } returns Unit

        deviceService.deleteAllDevice(USER_ID)
        coVerify(exactly = 1) { deviceRepository.deleteAll(any()) }
    }
}