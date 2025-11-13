package com.victorteka.weatherforecast.data.repository

import android.content.Context
import app.cash.turbine.test
import com.victorteka.weatherforecast.data.local.dao.WeatherDao
import com.victorteka.weatherforecast.data.local.entity.WeatherEntity
import com.victorteka.weatherforecast.data.remote.WeatherApiService
import com.victorteka.weatherforecast.data.remote.model.City
import com.victorteka.weatherforecast.data.remote.model.ForecastItem
import com.victorteka.weatherforecast.data.remote.model.Main
import com.victorteka.weatherforecast.data.remote.model.Weather
import com.victorteka.weatherforecast.data.remote.model.WeatherResponse
import com.victorteka.weatherforecast.data.remote.model.Wind
import com.victorteka.weatherforecast.domain.model.Location
import com.victorteka.weatherforecast.util.LocationManager
import com.victorteka.weatherforecast.util.isNetworkAvailable
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WeatherRepositoryTest {

    @MockK
    private lateinit var apiService: WeatherApiService

    @MockK
    private lateinit var weatherDao: WeatherDao

    @MockK
    private lateinit var locationManager: LocationManager

    @MockK
    private lateinit var context: Context

    private lateinit var repository: WeatherRepositoryImpl

    private val testLocation = Location(35.6762, 139.6503)
    private val testTimestamp = 1699891200L
    private val currentTime = System.currentTimeMillis()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockkStatic("com.victorteka.weatherforecast.util.NetworkUtilsKt")

        repository = WeatherRepositoryImpl(
            apiService = apiService,
            weatherDao = weatherDao,
            locationManager = locationManager,
            context = context
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getWeatherForecast should emit cached data first then fresh data when network available`() = runTest {
        val cachedEntities = createMockWeatherEntities(2)
        val apiResponse = createMockWeatherResponse(3)

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(cachedEntities)
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns apiResponse
        coEvery { weatherDao.deleteWeatherForLocation(testLocation.latitude, testLocation.longitude) } just Runs
        coEvery { weatherDao.insertWeatherForecast(any()) } just Runs
        coEvery { weatherDao.deleteOldForecasts(any()) } just Runs

        repository.getWeatherForecast().test {
            val firstResult = awaitItem()
            assertTrue(firstResult.isSuccess)
            assertEquals(2, firstResult.getOrNull()?.size)

            val secondResult = awaitItem()
            assertTrue(secondResult.isSuccess)
            assertEquals(3, secondResult.getOrNull()?.size)

            awaitComplete()
        }

        coVerify { locationManager.getCurrentLocation() }
        verify { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) }
        verify { isNetworkAvailable(context) }
        coVerify { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) }
        coVerify { weatherDao.insertWeatherForecast(any()) }
        coVerify { weatherDao.deleteOldForecasts(any()) }
    }

    @Test
    fun `getWeatherForecast should fetch from API when no cached data and network available`() = runTest {
        val apiResponse = createMockWeatherResponse(5)

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns apiResponse
        coEvery { weatherDao.deleteWeatherForLocation(any(), any()) } just Runs
        coEvery { weatherDao.insertWeatherForecast(any()) } just Runs
        coEvery { weatherDao.deleteOldForecasts(any()) } just Runs

        repository.getWeatherForecast().test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            assertEquals(5, result.getOrNull()?.size)

            awaitComplete()
        }

        coVerify { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) }
        coVerify { weatherDao.insertWeatherForecast(any()) }
    }

    @Test
    fun `getWeatherForecast should return cached data when network unavailable`() = runTest {
        val cachedEntities = createMockWeatherEntities(3)

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(cachedEntities)
        every { isNetworkAvailable(context) } returns false

        repository.getWeatherForecast().test {
            val result = awaitItem()

            assertTrue(result.isSuccess)
            assertEquals(3, result.getOrNull()?.size)

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { apiService.getWeatherForecast(any(), any()) }
    }

    @Test
    fun `getWeatherForecast should complete without emitting when network unavailable and no cache`() = runTest {
        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns false

        repository.getWeatherForecast().test {
            awaitComplete()
        }

        coVerify(exactly = 0) { apiService.getWeatherForecast(any(), any()) }
    }

    @Test
    fun `getWeatherForecast should return cached data when API call fails`() = runTest {
        val cachedEntities = createMockWeatherEntities(2)
        val apiException = Exception("Network error")

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(cachedEntities)
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) } throws apiException

        repository.getWeatherForecast().test {
            val firstResult = awaitItem()
            assertTrue(firstResult.isSuccess)
            assertEquals(2, firstResult.getOrNull()?.size)

            val secondResult = awaitItem()
            assertTrue(secondResult.isSuccess)
            assertEquals(2, secondResult.getOrNull()?.size)

            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast should emit failure when API fails and no cached data`() = runTest {
        val apiException = Exception("Network error")

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(testLocation.latitude, testLocation.longitude) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(testLocation.latitude, testLocation.longitude) } throws apiException

        repository.getWeatherForecast().test {
            val result = awaitItem()

            assertTrue(result.isFailure)
            assertEquals(apiException, result.exceptionOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast should use Tokyo location when getCurrentLocation fails`() = runTest {
        val tokyoLocation = Location(35.6764, 139.6500)
        val apiResponse = createMockWeatherResponse(3)

        coEvery { locationManager.getCurrentLocation() } returns Result.failure(Exception("Location error"))
        every { weatherDao.getWeatherForecast(tokyoLocation.latitude, tokyoLocation.longitude) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(tokyoLocation.latitude, tokyoLocation.longitude) } returns apiResponse
        coEvery { weatherDao.deleteWeatherForLocation(any(), any()) } just Runs
        coEvery { weatherDao.insertWeatherForecast(any()) } just Runs
        coEvery { weatherDao.deleteOldForecasts(any()) } just Runs

        repository.getWeatherForecast().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            awaitComplete()
        }

        coVerify { apiService.getWeatherForecast(35.6764, 139.6500) }
    }

    @Test
    fun `getWeatherForecast should delete old forecasts after successful API call`() = runTest {
        val apiResponse = createMockWeatherResponse(3)
        val capturedTimestamp = slot<Long>()

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(any(), any()) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(any(), any()) } returns apiResponse
        coEvery { weatherDao.deleteWeatherForLocation(any(), any()) } just Runs
        coEvery { weatherDao.insertWeatherForecast(any()) } just Runs
        coEvery { weatherDao.deleteOldForecasts(capture(capturedTimestamp)) } just Runs

        repository.getWeatherForecast().test {
            awaitItem()
            awaitComplete()
        }

        coVerify { weatherDao.deleteOldForecasts(any()) }

        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        assertTrue(Math.abs(capturedTimestamp.captured - sevenDaysAgo) < 60000)
    }

    @Test
    fun `getForecastById should return forecast when found in database`() = runTest {
        val forecastId = "test_id_123"
        val entity = createMockWeatherEntity(forecastId)

        every { weatherDao.getForecastById(forecastId) } returns flowOf(entity)

        repository.getForecastById(forecastId).test {
            val result = awaitItem()

            assertNotNull(result)
            assertTrue(result.isSuccess)
            assertEquals(forecastId, result.getOrNull()?.id)

            awaitComplete()
        }

        verify { weatherDao.getForecastById(forecastId) }
    }

    @Test
    fun `getForecastById should return failure when forecast not found`() = runTest {
        val forecastId = "non_existent_id"

        every { weatherDao.getForecastById(forecastId) } returns flowOf(null)

        repository.getForecastById(forecastId).test {
            val result = awaitItem()

            assertNotNull(result)
            assertTrue(result.isFailure)
            assertEquals("Forecast not found", result.exceptionOrNull()?.message)

            awaitComplete()
        }

        verify { weatherDao.getForecastById(forecastId) }
    }

    @Test
    fun `getWeatherForecast should delete existing data before inserting new data`() = runTest {
        val apiResponse = createMockWeatherResponse(3)

        coEvery { locationManager.getCurrentLocation() } returns Result.success(testLocation)
        every { weatherDao.getWeatherForecast(any(), any()) } returns flowOf(emptyList())
        every { isNetworkAvailable(context) } returns true
        coEvery { apiService.getWeatherForecast(any(), any()) } returns apiResponse
        coEvery { weatherDao.deleteWeatherForLocation(any(), any()) } just Runs
        coEvery { weatherDao.insertWeatherForecast(any()) } just Runs
        coEvery { weatherDao.deleteOldForecasts(any()) } just Runs

        repository.getWeatherForecast().test {
            awaitItem()
            awaitComplete()
        }

        coVerifyOrder {
            weatherDao.deleteWeatherForLocation(testLocation.latitude, testLocation.longitude)
            weatherDao.insertWeatherForecast(any())
        }
    }

    private fun createMockWeatherEntity(id: String = "test_id"): WeatherEntity {
        return WeatherEntity(
            id = id,
            timestamp = testTimestamp,
            temperature = 20.0,
            feelsLike = 18.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1013,
            humidity = 60,
            weatherMain = "Clear",
            weatherDescription = "clear sky",
            weatherIcon = "01d",
            windSpeed = 3.5,
            windDeg = 180,
            dateTime = "2023-11-13 12:00:00",
            cityName = "Tokyo",
            country = "JP",
            latitude = testLocation.latitude,
            longitude = testLocation.longitude,
            fetchedAt = currentTime
        )
    }

    private fun createMockWeatherEntities(count: Int): List<WeatherEntity> {
        return List(count) { index ->
            createMockWeatherEntity("test_id_$index")
        }
    }

    private fun createMockWeatherResponse(itemCount: Int): WeatherResponse {
        val forecastItems = List(itemCount) { index ->
            ForecastItem(
                dt = testTimestamp + (index * 3600),
                main = Main(
                    temp = 20.0 + index,
                    feelsLike = 18.0 + index,
                    tempMin = 15.0 + index,
                    tempMax = 25.0 + index,
                    pressure = 1013,
                    humidity = 60
                ),
                weather = listOf(
                    Weather(
                        id = 800,
                        main = "Clear",
                        description = "clear sky",
                        icon = "01d"
                    )
                ),
                wind = Wind(speed = 3.5, deg = 180),
                dtTxt = "2023-11-13 12:00:00"
            )
        }

        return WeatherResponse(
            list = forecastItems,
            city = City(name = "Tokyo", country = "JP")
        )
    }
}