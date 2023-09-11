package com.example.techsupport

sealed class Screen(val route: String) {
    // AUTH SCREENS
    object ScLogin : Screen("login")
    object ScRegister : Screen("register")
    object ScForgotPassword : Screen("forgot_password")
    object ScTermsAndConditions : Screen("terms_and_conditions")

    // MAIN SCREEN AND USER SCREENS
    object ScMain : Screen("main")


    // ADMIN SCREENS
    object ScAdmin : Screen("admin")
    object ScManageDevices : Screen("manage_devices")
    object ScManageKnowledgeBase : Screen("manage_knowledge_base")
    object ScCreateKnowledgeBase : Screen("create_knowledge_base")
    object ScManageStaffs : Screen("manage_staffs")
    object ScCreateStaff : Screen("create_staff")


    // Function to build route with arguments
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
