package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.ChatMessage
import com.hermes.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(sessionId: String): Flow<List<ChatMessage>> {
        return chatRepository.observeMessages(sessionId)
    }
}
