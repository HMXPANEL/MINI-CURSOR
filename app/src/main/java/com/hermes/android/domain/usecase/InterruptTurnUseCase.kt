package com.hermes.android.domain.usecase

import com.hermes.android.domain.repository.ChatRepository
import javax.inject.Inject

class InterruptTurnUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(turnId: String) {
        chatRepository.interruptTurn(turnId)
    }
}
