package com.hermes.android.domain.usecase

import com.hermes.android.domain.repository.ChatRepository
import javax.inject.Inject

class ApproveToolUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(turnId: String, toolCallId: String, approved: Boolean) {
        chatRepository.approveTool(turnId, toolCallId, approved)
    }
}
