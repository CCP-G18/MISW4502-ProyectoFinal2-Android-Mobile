package com.g18.ccp.presentation.seller.customervisit.register

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.customervisit.VisitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class SellerRegisterVisitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: SellerRegisterVisitViewModel
    private val visitRepository = mockk<VisitRepository>()
    private val customerRepository = mockk<CustomerRepository>()
    private val savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to "123"))

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock Log methods to avoid unmocked errors
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any<Throwable>()) } returns 0
        every { Log.i(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        unmockkStatic(Log::class)
    }

    @Test
    fun `given default state when loadInitialData then uiState has default customerName and a formatted date`() =
        testScope.runTest {
            // dado que el repositorio no retorna ningún cliente
            coEvery { customerRepository.getCustomerById("123") } returns flowOf(null)

            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // cuando cargamos datos iniciales
            viewModel.loadInitialData()
            advanceUntilIdle()

            // entonces el estado debe tener el nombre por defecto y fecha en formato dd/MM/yyyy
            val state = viewModel.uiState.value
            assertEquals("Cliente ID: 123", state.customerName)
            assertTrue(state.selectedDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}")))
        }

    @Test
    fun `given date millis in past when onDateSelected then update selectedDate without error`() =
        testScope.runTest {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // dado un instante fijo (1 enero 1970 UTC)
            val pastMillis = 0L

            // cuando seleccionamos una fecha pasada
            viewModel.onDateSelected(pastMillis)

            // entonces el selectedDate se formatea correctamente y no hay error
            val state = viewModel.uiState.value
            assertEquals("01/01/1970", state.selectedDate)
            assertNull(state.errorMessage)
        }

    @Test
    fun `given date millis in future when onDateSelected then set errorMessage`() =
        testScope.runTest {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // instante en el futuro (hoy + 1 día UTC)
            val tomorrowUtc = LocalDate.now(ZoneOffset.UTC).plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

            // cuando seleccionamos una fecha futura
            viewModel.onDateSelected(tomorrowUtc)

            // entonces errorMessage se establece
            val state = viewModel.uiState.value
            assertEquals("No se puede seleccionar una fecha futura.", state.errorMessage)
        }

    @Test
    fun `given observations changed when onObservationsChanged then update observations`() =
        testScope.runTest {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // cuando cambiamos observaciones
            viewModel.onObservationsChanged("Nueva observación")

            // entonces se actualiza en el estado
            val state = viewModel.uiState.value
            assertEquals("Nueva observación", state.observations)
        }

    @Test
    fun `given showDatePicker true when onShowDatePicker then update showDatePicker flag`() =
        testScope.runTest {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // cuando mostramos el date picker
            viewModel.onShowDatePicker(true)

            // entonces el flag showDatePicker es true
            val state = viewModel.uiState.value
            assertTrue(state.showDatePicker)
        }

    @Test
    fun `given invalid date when saveVisit then show format error and do not call repository`() =
        testScope.runTest {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            // selectedDate inicial es vacío, invalid
            var navCalled = false
            viewModel.saveVisit { navCalled = true }

            // entonces error de formato y no navega
            val state = viewModel.uiState.value
            assertFalse(navCalled)
            assertEquals("Formato de fecha inválido.", state.errorMessage)
            coVerify(exactly = 0) { visitRepository.registerVisit(any(), any(), any()) }
        }

    @Test
    fun `given valid date when saveVisit on success then clear loading and navigate`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            viewModel.onDateSelected(0L)

            coEvery {
                visitRepository.registerVisit(
                    "123",
                    "1970-01-01",
                    any()
                )
            } returns Result.success(mockk {
                every { message } returns "Visita registrada"
            })

            var navCalled = false
            viewModel.saveVisit(mainDispatcherRule.testDispatcher) { navCalled = true }
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertNull(state.errorMessage)
            assertTrue(navCalled)
            coVerify(exactly = 1) { visitRepository.registerVisit("123", "1970-01-01", " ") }
        }

    @Test
    fun `given valid date when saveVisit on failure then show errorMessage`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel =
                SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

            viewModel.onDateSelected(0L)
            coEvery { visitRepository.registerVisit(any(), any(), any()) } returns Result.failure(
                RuntimeException("fail")
            )

            viewModel.saveVisit(mainDispatcherRule.testDispatcher) {}
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("Error al guardar visita: fail", state.errorMessage)
        }

    @Test
    fun `given errorMessage present when clearErrorMessage then clear it`() = testScope.runTest {
        viewModel =
            SellerRegisterVisitViewModel(savedStateHandle, visitRepository, customerRepository)

        // aseguramos un error
        val futureMillis = LocalDate.now(ZoneOffset.UTC).plusDays(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        viewModel.onDateSelected(futureMillis)
        assertNotNull(viewModel.uiState.value.errorMessage)

        // cuando limpiamos
        viewModel.clearErrorMessage()

        // entonces errorMessage es null
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
