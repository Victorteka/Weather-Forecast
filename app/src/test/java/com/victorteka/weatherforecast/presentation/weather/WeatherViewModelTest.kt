package com.victorteka.weatherforecast.presentation.weather

import app.cash.turbine.test
import com.victorteka.weatherforecast.MainDispatcherRule
import com.victorteka.weatherforecast.domain.model.WeatherForecast
import com.victorteka.weatherforecast.domain.repository.WeatherRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var repository: WeatherRepository

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        coEvery { repository.getWeatherForecast() } returns flowOf()

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.forecasts.isEmpty())
            assertEquals(null, state.errorMsg)
        }
    }

    @Test
    fun `loadWeatherForCurrentLocation should set loading state initially`() = runTest {
        val mockForecasts = listOf(createMockWeatherForecast())
        coEvery { repository.getWeatherForecast() } returns flowOf(Result.success(mockForecasts))

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            viewModel.loadWeatherForCurrentLocation()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(1, successState.forecasts.size)
        }
    }

    @Test
    fun `loadWeatherForCurrentLocation should update state with forecasts on success`() = runTest {
        val mockForecasts = listOf(
            createMockWeatherForecast("id1", "Tokyo"),
            createMockWeatherForecast("id2", "Osaka")
        )
        coEvery { repository.getWeatherForecast() } returns flowOf(Result.success(mockForecasts))

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            skipItems(1)

            viewModel.loadWeatherForCurrentLocation()

            skipItems(1)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(2, successState.forecasts.size)
            assertEquals("Tokyo", successState.forecasts[0].cityName)
            assertEquals("Osaka", successState.forecasts[1].cityName)
            assertEquals(null, successState.errorMsg)
        }
    }

    @Test
    fun `loadWeatherForCurrentLocation should update state with error message on failure`() = runTest {
        val errorMessage = "Network error"
        coEvery { repository.getWeatherForecast() } returns flowOf(
            Result.failure(Exception(errorMessage))
        )

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            skipItems(1)

            viewModel.loadWeatherForCurrentLocation()

            skipItems(1)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.forecasts.isEmpty())
            assertEquals(errorMessage, errorState.errorMsg)
        }
    }

    @Test
    fun `loadWeatherForCurrentLocation should handle exception in flow`() = runTest {
        val exception = RuntimeException("Unexpected error")
        coEvery { repository.getWeatherForecast() } returns flow {
            throw exception
        }

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            skipItems(1)

            viewModel.loadWeatherForCurrentLocation()

            skipItems(1)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("Unexpected error", errorState.errorMsg)
        }
    }

    @Test
    fun `loadWeatherForCurrentLocation should use default error message when exception message is null`() = runTest {
        coEvery { repository.getWeatherForecast() } returns flowOf(
            Result.failure(Exception())
        )

        viewModel = WeatherViewModel(repository)

        viewModel.uiState.test {
            skipItems(1)

            viewModel.loadWeatherForCurrentLocation()

            skipItems(1)

            val errorState = awaitItem()
            assertEquals("Something went wrong", errorState.errorMsg)
        }
    }

    private fun createMockWeatherForecast(
        id: String = "test_id",
        cityName: String = "Tokyo"
    ): WeatherForecast {
        return WeatherForecast(
            id = id,
            timestamp = 1699891200L,
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
            cityName = cityName,
            country = "JP"
        )
    }
}