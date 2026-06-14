package com.lhzkml.jasmineagent.core.domain.usecase

import com.lhzkml.jasmineagent.core.data.AgentRepository
import com.lhzkml.jasmineagent.core.data.AgentRepositoryException
import com.lhzkml.jasmineagent.core.database.Agent
import com.lhzkml.jasmineagent.core.database.AgentStatus
import com.lhzkml.jasmineagent.core.domain.validation.ValidationError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddAgentUseCaseTest {

  private lateinit var useCase: AddAgentUseCase
  private lateinit var fakeRepository: FakeAgentRepository

  @Before
  fun setup() {
    fakeRepository = FakeAgentRepository()
    useCase = AddAgentUseCase(fakeRepository)
  }

  @Test
  fun invoke_withValidName_returnsSuccess() = runTest {
    val result = useCase("ValidAgent")

    assertTrue("Result should be Success", result is AddAgentResult.Success)
    assertEquals("Name should match", "ValidAgent", (result as AddAgentResult.Success).name)
    assertTrue("Agent should be added", fakeRepository.addedAgents.contains("ValidAgent"))
  }

  @Test
  fun invoke_withValidNameWithSpaces_trimsAndReturnsSuccess() = runTest {
    val result = useCase("  ValidAgent  ")

    assertTrue("Result should be Success", result is AddAgentResult.Success)
    assertEquals("Name should be trimmed", "ValidAgent", (result as AddAgentResult.Success).name)
  }

  @Test
  fun invoke_withEmptyName_returnsValidationFailure() = runTest {
    val result = useCase("")

    assertTrue("Result should be ValidationFailure", result is AddAgentResult.ValidationFailure)
    assertEquals(
      "Error should be EmptyInput",
      ValidationError.EmptyInput,
      (result as AddAgentResult.ValidationFailure).error,
    )
  }

  @Test
  fun invoke_withTooShortName_returnsValidationFailure() = runTest {
    val result = useCase("A")

    assertTrue("Result should be ValidationFailure", result is AddAgentResult.ValidationFailure)
    val error = (result as AddAgentResult.ValidationFailure).error
    assertTrue("Error should be TooShort", error is ValidationError.TooShort)
  }

  @Test
  fun invoke_withTooLongName_returnsValidationFailure() = runTest {
    val result = useCase("A".repeat(101))

    assertTrue("Result should be ValidationFailure", result is AddAgentResult.ValidationFailure)
    val error = (result as AddAgentResult.ValidationFailure).error
    assertTrue("Error should be TooLong", error is ValidationError.TooLong)
  }

  @Test
  fun invoke_withInvalidCharacters_returnsValidationFailure() = runTest {
    val result = useCase("Agent@#$")

    assertTrue("Result should be ValidationFailure", result is AddAgentResult.ValidationFailure)
    val error = (result as AddAgentResult.ValidationFailure).error
    assertTrue("Error should be InvalidCharacters", error is ValidationError.InvalidCharacters)
  }

  @Test
  fun invoke_withDuplicateName_returnsValidationFailure() = runTest {
    fakeRepository.setShouldThrowIllegalArgument(true)

    val result = useCase("DuplicateAgent")

    assertTrue("Result should be ValidationFailure", result is AddAgentResult.ValidationFailure)
    val error = (result as AddAgentResult.ValidationFailure).error
    assertTrue("Error should be AlreadyExists", error is ValidationError.AlreadyExists)
    assertEquals(
      "Name should match",
      "DuplicateAgent",
      (error as ValidationError.AlreadyExists).name,
    )
  }

  @Test
  fun invoke_withRepositoryError_returnsRepositoryFailure() = runTest {
    fakeRepository.setShouldThrowGenericException(true)

    val result = useCase("ValidAgent")

    assertTrue("Result should be RepositoryFailure", result is AddAgentResult.RepositoryFailure)
    val failure = result as AddAgentResult.RepositoryFailure
    assertEquals("Error message should match", "Repository error", failure.message)
  }

  private class FakeAgentRepository : AgentRepository {
    val addedAgents = mutableListOf<String>()
    private var shouldThrowIllegalArgument = false
    private var shouldThrowGenericException = false

    override val agents = flowOf<List<String>>(emptyList())

    override fun getAgents(limit: Int): Flow<List<Agent>> =
      flowOf(
        addedAgents.take(limit).mapIndexed { index, name -> Agent(uid = index + 1, name = name) }
      )

    override suspend fun getById(uid: Int): Agent? =
      addedAgents.getOrNull(uid - 1)?.let { Agent(uid = uid, name = it) }

    override suspend fun getByName(name: String): Agent? =
      addedAgents.firstOrNull { it == name }?.let { Agent(name = it) }

    override suspend fun add(name: String) {
      when {
        shouldThrowIllegalArgument -> throw IllegalArgumentException("Already exists")
        shouldThrowGenericException -> throw AgentRepositoryException("Repository error")
        else -> addedAgents.add(name)
      }
    }

    override suspend fun updateStatus(uid: Int, status: AgentStatus) = Unit

    override suspend fun delete(uid: Int) {
      addedAgents.removeAt(uid - 1)
    }

    override suspend fun getActiveCount(): Int = addedAgents.size

    fun setShouldThrowIllegalArgument(shouldThrow: Boolean) {
      shouldThrowIllegalArgument = shouldThrow
    }

    fun setShouldThrowGenericException(shouldThrow: Boolean) {
      shouldThrowGenericException = shouldThrow
    }
  }
}
