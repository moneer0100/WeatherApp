import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.FakeLocalDataSource
import com.example.weatherapp.FakeREpo
import com.example.weatherapp.FakeRemoteDataSource
import com.example.weatherapp.Model.City
import com.example.weatherapp.Model.Coord
import com.example.weatherapp.Model.Forecast

import com.example.weatherapp.network.ResponseState
import com.example.weatherapp.ui.home.viewModel.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest=Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeRepo: FakeREpo

    private val currentWeather = Welcome(
        501,
        Coord(10.99, 44.34),
        listOf(Weather(501, "Rain", "moderate rain", "10d")),
        "stations",
        Main(285.95, 285.74, 284.94, 287.76, 1009, 94, 1009, 942),
        8412,
        Wind(2.87, 167, 5.81),
        Clouds(100),
        1728380785,
        Sys(2, 2044440, "IT", 1728364943, 1728405860),
        7200,
        "Zocca",
        200
    )

    private val weatherForecast = Forecast(
        "200",
        0,
        40,
        emptyList(),
        City(0, "", Coord(0.0, 0.0), "", 0, 0, 0, 0)
    )

    @Before
    fun setUp() {
        fakeRepo = FakeREpo(FakeRemoteDataSource(currentWeather, weatherForecast), FakeLocalDataSource())
        homeViewModel = HomeViewModel(fakeRepo)
    }

    @Test
    fun getForcast_Success() = runBlockingTest {
        // Given
        val latitude = 0.0 // Use valid latitude
        val longitude = 0.0 // Use valid longitude
        val language = "en"
        val units = "metric"

        // When
      val result=  homeViewModel.getForcastWeatherRespons(latitude, longitude, language, units)

        // Then

        assertThat(result , not(nullValue()))

    }
    @Test
    fun getCurrent_Success() = runBlockingTest{
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val language = "en"
        val units = "metric"

        // When
        val result=  homeViewModel.getCurrentWeatherResponse(latitude, longitude, language, units)

        // Then

        assertThat(result , not(nullValue()))

    }
}
