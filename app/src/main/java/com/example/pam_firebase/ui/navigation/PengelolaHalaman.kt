package com.example.pam_firebase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pam_firebase.ui.view.DetailView
import com.example.pam_firebase.ui.view.HomeScreen
import com.example.pam_firebase.ui.view.InsertMhsView
import com.example.pam_firebase.ui.theme.UpdateView
import com.example.pam_firebase.ui.viewmodel.PenyediaViewModel
import com.example.pam_firebase.ui.theme.UpdateViewModel

@Composable
fun PengelolaHalaman(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = Modifier
    ) {
        composable(DestinasiHome.route) {
            HomeScreen(
                navigateToItemEntry = {
                    navController.navigate(DestinasiInsert.route)
                },
                onDetailClick = { nim ->
                    navController.navigate(
                        DestinasiDetail.routesWithArg.replace(
                            "{${DestinasiDetail.NIM}}",
                            nim
                        )
                    )
                }
            )
        }
        composable(DestinasiInsert.route) {
            InsertMhsView(
                onBack = { navController.popBackStack() },
                onNavigate = {
                    navController.navigate(DestinasiHome.route)
                }
            )
        }
        composable(
            DestinasiDetail.routesWithArg,
            arguments = listOf(
                navArgument(DestinasiDetail.NIM) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val nim = backStackEntry.arguments?.getString(DestinasiDetail.NIM)
            nim?.let {
                DetailView(
                    nim = nim,
                    onBack = { navController.popBackStack() },
                    onUpdate = { nim ->
                        navController.navigate(
                            DestinasiUpdate.routeWithArg.replace(
                                "{${DestinasiUpdate.NIM}}",
                                nim
                            )
                        )
                    },
                )
            }
        }
        composable(
            DestinasiUpdate.routeWithArg,
            arguments = listOf(
                navArgument(DestinasiUpdate.NIM) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val nim = backStackEntry.arguments?.getString(DestinasiUpdate.NIM) ?: ""
            println("Rendering UpdateView for nim: $nim")
            val viewModel: UpdateViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val mahasiswaState by viewModel.mahasiswaState.collectAsState(initial = null)
            SideEffect {
                viewModel.getMhs(nim)
            }
            mahasiswaState?.let { mahasiswa ->
                UpdateView(
                    mahasiswa = mahasiswa,
                    onUpdateSuccess = {
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}