package com.hermes.android.domain.usecase

import com.hermes.android.data.websocket.HermesConnectionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val connectionManager: HermesConnectionManager
) {
    operator fun invoke(): Flow<HermesConnectionManager.State> {
        return connectionManager.state
    }
}
