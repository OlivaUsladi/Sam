package com.example.myapplication.Finance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.Finance.ui.accounts.AccountsScreen
import com.example.myapplication.Finance.ui.analytics.AnalyticsScreen
import com.example.myapplication.Finance.ui.bankreport.BankReportsScreen
import com.example.myapplication.Finance.ui.goal.GoalDetailScreen
import com.example.myapplication.Finance.ui.goal.GoalEditScreen
import com.example.myapplication.Finance.ui.history.HistoryScreen
import com.example.myapplication.Finance.ui.sources.SourcesScreen
import com.example.myapplication.Finance.ui.tag.AssignTagTransactionsScreen
import com.example.myapplication.Finance.ui.tag.TagEditScreen
import com.example.myapplication.Finance.ui.tagsgoals.TagsGoalsScreen
import com.example.myapplication.Finance.ui.transaction.TransactionEditScreen

@Composable
fun FinanceNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = FinanceRoutes.Accounts.route,
    ) {
        composable(FinanceRoutes.Accounts.route)   { AccountsScreen(navController) }
        composable(FinanceRoutes.Analytics.route)  { AnalyticsScreen(navController) }
        composable(FinanceRoutes.Categories.route) { TagsGoalsScreen(navController) }

        composable(
            FinanceRoutes.History.route,
            arguments = listOf(
                navArgument(FinanceRoutes.History.ARG_SOURCE_ID) {
                    type = NavType.IntType
                    defaultValue = Int.MIN_VALUE
                },
            ),
        ) {
            val raw = it.arguments?.getInt(FinanceRoutes.History.ARG_SOURCE_ID) ?: Int.MIN_VALUE
            HistoryScreen(navController, sourceId = raw.takeIf { v -> v != Int.MIN_VALUE })
        }

        composable(FinanceRoutes.Sources.route)    { SourcesScreen(navController) }
        composable(FinanceRoutes.BankReports.route){ BankReportsScreen(navController) }

        composable(FinanceRoutes.NewTx.route) { TransactionEditScreen(navController, id = null) }
        composable(
            FinanceRoutes.EditTx.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            val id = it.arguments?.getInt("id")
            TransactionEditScreen(navController, id = id)
        }

        composable(FinanceRoutes.NewTag.route) { TagEditScreen(navController, id = null) }
        composable(
            FinanceRoutes.EditTag.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            val id = it.arguments?.getInt("id")
            TagEditScreen(navController, id = id)
        }
        composable(
            FinanceRoutes.AssignTagTransactions.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            val id = it.arguments?.getInt("id") ?: 0
            AssignTagTransactionsScreen(navController, tagId = id)
        }

        composable(FinanceRoutes.NewGoal.route) { GoalEditScreen(navController, id = null) }
        composable(
            FinanceRoutes.EditGoal.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            val id = it.arguments?.getInt("id")
            GoalEditScreen(navController, id = id)
        }
        composable(
            FinanceRoutes.GoalDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            val id = it.arguments?.getInt("id")
            GoalDetailScreen(navController, id = id)
        }
    }
}
