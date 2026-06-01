package com.example.data.common.sync

import com.example.data.common.network.NetworkMonitor
import com.example.domain.Finance.repository.FinanceRepository
import com.example.domain.Recipes.repository.RecipeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncManager(
    private val networkMonitor: NetworkMonitor,
    private val financeRepository: FinanceRepository,
    private val recipeRepository: RecipeRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val POLL_INTERVAL_MS: Long = 100_000L
    }

    fun start() {
        // Событийный триггер.
        scope.launch {
            networkMonitor.isOnline
                .collectLatest { online ->
                    if (online) {
                        runCatching { financeRepository.syncNow() }
                        runCatching { recipeRepository.syncShoppingListsNow() }
                    }
                }
        }

        // Периодический триггер
        scope.launch {
            while (true) {
                delay(POLL_INTERVAL_MS)
                if (networkMonitor.isOnline.value) {
                    runCatching { financeRepository.syncNow() }
                    runCatching { recipeRepository.syncShoppingListsNow() }
                }
            }
        }
    }
}
