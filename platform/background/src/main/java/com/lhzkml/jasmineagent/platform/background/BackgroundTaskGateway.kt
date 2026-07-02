package com.lhzkml.jasmineagent.platform.background

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.lhzkml.jasmineagent.platform.notifications.NotificationGateway
import com.lhzkml.jasmineagent.platform.notifications.PlatformNotificationSpec
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class BackgroundNetworkType {
  NotRequired,
  Connected,
  Unmetered,
  NotRoaming,
  Metered,
}

data class BackgroundWorkConstraints(
  val networkType: BackgroundNetworkType = BackgroundNetworkType.NotRequired,
  val requiresCharging: Boolean = false,
  val requiresBatteryNotLow: Boolean = false,
  val requiresStorageNotLow: Boolean = false,
  val requiresDeviceIdle: Boolean = false,
)

enum class BackgroundBackoffKind {
  Linear,
  Exponential,
}

data class BackgroundBackoffPolicy(
  val kind: BackgroundBackoffKind = BackgroundBackoffKind.Exponential,
  val delay: Duration = Duration.ofSeconds(30),
)

enum class BackgroundExistingWorkPolicy {
  Keep,
  Replace,
  Append,
  AppendOrReplace,
}

enum class BackgroundExistingPeriodicWorkPolicy {
  Keep,
  Update,
}

enum class BackgroundOutOfQuotaPolicy {
  RunAsNonExpeditedWorkRequest,
  DropWorkRequest,
}

data class OneTimeBackgroundWorkSpec(
  val workerClass: KClass<out ListenableWorker>,
  val inputData: Data = Data.EMPTY,
  val constraints: BackgroundWorkConstraints = BackgroundWorkConstraints(),
  val tags: Set<String> = emptySet(),
  val initialDelay: Duration = Duration.ZERO,
  val backoffPolicy: BackgroundBackoffPolicy? = null,
  val uniqueName: String? = null,
  val uniquePolicy: BackgroundExistingWorkPolicy = BackgroundExistingWorkPolicy.Keep,
  val expedited: Boolean = false,
  val outOfQuotaPolicy: BackgroundOutOfQuotaPolicy =
    BackgroundOutOfQuotaPolicy.RunAsNonExpeditedWorkRequest,
)

data class PeriodicBackgroundWorkSpec(
  val workerClass: KClass<out ListenableWorker>,
  val repeatInterval: Duration,
  val inputData: Data = Data.EMPTY,
  val constraints: BackgroundWorkConstraints = BackgroundWorkConstraints(),
  val tags: Set<String> = emptySet(),
  val initialDelay: Duration = Duration.ZERO,
  val backoffPolicy: BackgroundBackoffPolicy? = null,
  val uniqueName: String? = null,
  val uniquePolicy: BackgroundExistingPeriodicWorkPolicy =
    BackgroundExistingPeriodicWorkPolicy.Keep,
)

enum class BackgroundWorkState {
  Enqueued,
  Running,
  Succeeded,
  Failed,
  Blocked,
  Cancelled,
}

data class BackgroundWorkInfo(
  val id: UUID,
  val state: BackgroundWorkState,
  val tags: Set<String>,
  val runAttemptCount: Int,
  val progress: Data,
  val outputData: Data,
)

interface BackgroundTaskGateway {
  fun enqueueOneTime(spec: OneTimeBackgroundWorkSpec): UUID

  fun enqueuePeriodic(spec: PeriodicBackgroundWorkSpec): UUID

  fun observe(id: UUID): Flow<BackgroundWorkInfo?>

  fun observeUniqueWork(name: String): Flow<List<BackgroundWorkInfo>>

  fun cancel(id: UUID)

  fun cancelUniqueWork(name: String)

  fun cancelAllByTag(tag: String)

  fun pruneFinishedWork()
}

interface ForegroundWorkInfoFactory {
  fun createForegroundInfo(
    notificationSpec: PlatformNotificationSpec,
    foregroundServiceType: Int = 0,
  ): ForegroundInfo
}

class WorkManagerBackgroundTaskGateway @Inject constructor(@ApplicationContext context: Context) :
  BackgroundTaskGateway {
  private val workManager = WorkManager.getInstance(context)

  override fun enqueueOneTime(spec: OneTimeBackgroundWorkSpec): UUID {
    val request =
      OneTimeWorkRequest.Builder(spec.workerClass.java)
        .applyCommon(
          spec.inputData,
          spec.constraints,
          spec.tags,
          spec.initialDelay,
          spec.backoffPolicy,
        )
        .apply {
          if (spec.expedited) {
            setExpedited(spec.outOfQuotaPolicy.toWorkPolicy())
          }
        }
        .build()

    if (spec.uniqueName == null) {
      workManager.enqueue(request)
    } else {
      workManager.enqueueUniqueWork(spec.uniqueName, spec.uniquePolicy.toWorkPolicy(), request)
    }

    return request.id
  }

  override fun enqueuePeriodic(spec: PeriodicBackgroundWorkSpec): UUID {
    requireValidPeriodicInterval(spec.repeatInterval)

    val request =
      PeriodicWorkRequest.Builder(
          spec.workerClass.java,
          spec.repeatInterval.toMillis(),
          TimeUnit.MILLISECONDS,
        )
        .applyCommon(
          spec.inputData,
          spec.constraints,
          spec.tags,
          spec.initialDelay,
          spec.backoffPolicy,
        )
        .build()

    if (spec.uniqueName == null) {
      workManager.enqueue(request)
    } else {
      workManager.enqueueUniquePeriodicWork(
        spec.uniqueName,
        spec.uniquePolicy.toWorkPolicy(),
        request,
      )
    }

    return request.id
  }

  override fun observe(id: UUID): Flow<BackgroundWorkInfo?> =
    workManager.getWorkInfoByIdLiveData(id).asFlow().map { it?.toBackgroundWorkInfo() }

  override fun observeUniqueWork(name: String): Flow<List<BackgroundWorkInfo>> =
    workManager.getWorkInfosForUniqueWorkLiveData(name).asFlow().map { infos ->
      infos.map { it.toBackgroundWorkInfo() }
    }

  override fun cancel(id: UUID) {
    workManager.cancelWorkById(id)
  }

  override fun cancelUniqueWork(name: String) {
    workManager.cancelUniqueWork(name)
  }

  override fun cancelAllByTag(tag: String) {
    workManager.cancelAllWorkByTag(tag)
  }

  override fun pruneFinishedWork() {
    workManager.pruneWork()
  }
}

class WorkManagerForegroundWorkInfoFactory
@Inject
constructor(private val notificationGateway: NotificationGateway) : ForegroundWorkInfoFactory {
  override fun createForegroundInfo(
    notificationSpec: PlatformNotificationSpec,
    foregroundServiceType: Int,
  ): ForegroundInfo {
    notificationGateway.ensureDefaultChannels()
    val notification = notificationGateway.buildNotification(notificationSpec)

    return if (foregroundServiceType == 0) {
      ForegroundInfo(notificationSpec.id, notification)
    } else {
      ForegroundInfo(notificationSpec.id, notification, foregroundServiceType)
    }
  }
}

internal fun requireValidPeriodicInterval(repeatInterval: Duration) {
  require(!repeatInterval.isNegative && !repeatInterval.isZero) {
    "Periodic work interval must be positive."
  }
  require(repeatInterval.toMillis() >= PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
    "Periodic work interval must be at least ${PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS} ms."
  }
}

private fun BackgroundWorkConstraints.toWorkConstraints(): Constraints =
  Constraints.Builder()
    .setRequiredNetworkType(networkType.toWorkNetworkType())
    .setRequiresCharging(requiresCharging)
    .setRequiresBatteryNotLow(requiresBatteryNotLow)
    .setRequiresStorageNotLow(requiresStorageNotLow)
    .setRequiresDeviceIdle(requiresDeviceIdle)
    .build()

private fun BackgroundNetworkType.toWorkNetworkType(): NetworkType =
  when (this) {
    BackgroundNetworkType.NotRequired -> NetworkType.NOT_REQUIRED
    BackgroundNetworkType.Connected -> NetworkType.CONNECTED
    BackgroundNetworkType.Unmetered -> NetworkType.UNMETERED
    BackgroundNetworkType.NotRoaming -> NetworkType.NOT_ROAMING
    BackgroundNetworkType.Metered -> NetworkType.METERED
  }

private fun BackgroundBackoffKind.toWorkPolicy(): BackoffPolicy =
  when (this) {
    BackgroundBackoffKind.Linear -> BackoffPolicy.LINEAR
    BackgroundBackoffKind.Exponential -> BackoffPolicy.EXPONENTIAL
  }

private fun BackgroundExistingWorkPolicy.toWorkPolicy(): ExistingWorkPolicy =
  when (this) {
    BackgroundExistingWorkPolicy.Keep -> ExistingWorkPolicy.KEEP
    BackgroundExistingWorkPolicy.Replace -> ExistingWorkPolicy.REPLACE
    BackgroundExistingWorkPolicy.Append -> ExistingWorkPolicy.APPEND
    BackgroundExistingWorkPolicy.AppendOrReplace -> ExistingWorkPolicy.APPEND_OR_REPLACE
  }

private fun BackgroundExistingPeriodicWorkPolicy.toWorkPolicy(): ExistingPeriodicWorkPolicy =
  when (this) {
    BackgroundExistingPeriodicWorkPolicy.Keep -> ExistingPeriodicWorkPolicy.KEEP
    BackgroundExistingPeriodicWorkPolicy.Update -> ExistingPeriodicWorkPolicy.UPDATE
  }

private fun BackgroundOutOfQuotaPolicy.toWorkPolicy(): OutOfQuotaPolicy =
  when (this) {
    BackgroundOutOfQuotaPolicy.RunAsNonExpeditedWorkRequest ->
      OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
    BackgroundOutOfQuotaPolicy.DropWorkRequest -> OutOfQuotaPolicy.DROP_WORK_REQUEST
  }

private fun <B : WorkRequest.Builder<*, *>> B.applyCommon(
  inputData: Data,
  constraints: BackgroundWorkConstraints,
  tags: Set<String>,
  initialDelay: Duration,
  backoffPolicy: BackgroundBackoffPolicy?,
): B = apply {
  setInputData(inputData)
  setConstraints(constraints.toWorkConstraints())
  if (!initialDelay.isZero) {
    setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
  }
  backoffPolicy?.let {
    setBackoffCriteria(it.kind.toWorkPolicy(), it.delay.toMillis(), TimeUnit.MILLISECONDS)
  }
  tags.forEach(::addTag)
}

private fun WorkInfo.toBackgroundWorkInfo(): BackgroundWorkInfo =
  BackgroundWorkInfo(
    id = id,
    state = state.toBackgroundWorkState(),
    tags = tags,
    runAttemptCount = runAttemptCount,
    progress = progress,
    outputData = outputData,
  )

private fun WorkInfo.State.toBackgroundWorkState(): BackgroundWorkState =
  when (this) {
    WorkInfo.State.ENQUEUED -> BackgroundWorkState.Enqueued
    WorkInfo.State.RUNNING -> BackgroundWorkState.Running
    WorkInfo.State.SUCCEEDED -> BackgroundWorkState.Succeeded
    WorkInfo.State.FAILED -> BackgroundWorkState.Failed
    WorkInfo.State.BLOCKED -> BackgroundWorkState.Blocked
    WorkInfo.State.CANCELLED -> BackgroundWorkState.Cancelled
  }
