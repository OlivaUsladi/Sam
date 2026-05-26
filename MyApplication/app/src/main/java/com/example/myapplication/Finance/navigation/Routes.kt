package com.example.myapplication.Finance.navigation

sealed class FinanceRoutes(val route: String) {
    object Accounts    : FinanceRoutes("accounts")

    object Analytics   : FinanceRoutes("analytics")

    object Categories  : FinanceRoutes("categories")

    object History : FinanceRoutes("history?sourceId={sourceId}") {
        const val ARG_SOURCE_ID = "sourceId"
        fun buildForSource(sourceId: Int) = "history?sourceId=$sourceId"
    }

    object Sources     : FinanceRoutes("sources")
    object BankReports : FinanceRoutes("bank_reports")

    object NewTx       : FinanceRoutes("transaction/new")
    object EditTx      : FinanceRoutes("transaction/{id}") {
        fun build(id: Int) = "transaction/$id"
    }

    object NewTag      : FinanceRoutes("tag/new")
    object EditTag     : FinanceRoutes("tag/{id}") {
        fun build(id: Int) = "tag/$id"
    }
    object AssignTagTransactions : FinanceRoutes("tag/{id}/assign") {
        fun build(id: Int) = "tag/$id/assign"
    }

    object NewGoal     : FinanceRoutes("goal/new")
    object GoalDetail  : FinanceRoutes("goal/{id}") {
        fun build(id: Int) = "goal/$id"
    }
}
