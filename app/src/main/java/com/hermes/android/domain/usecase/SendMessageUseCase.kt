package com.hermes.android.domain.usecase

import com.hermes.android.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(sessionId: String, content: String, attachments: List<String> = emptyList()) {
        chatRepository.sendMessage(sessionId, content, attachments)
    }
}
