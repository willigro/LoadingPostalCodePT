import Depends.Databinding.implementDatabinding
import Depends.Fragment.implementFragmentAndroidKtxAndNavigationCompose
import Depends.Hilt.implementHilt
import Depends.Kotlin.implementKotlinForModule
import Depends.Lottie.implementLottie
import Depends.Module.implementAllModules
import Depends.Retrofit.implementRetrofit
import Depends.Robbie.implementRobbie
import Depends.Room.implementRoom
import Depends.ViewModel.implementViewModel
import Depends.Views.implementLayouts
import Depends.Worker.implementWorker

plugins {
    id(Depends.Plugins.HILT)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // =========== Modules ==============
    implementAllModules(Modules.app)

    // =========== Kotlin ==============
    implementKotlinForModule()

    // =========== Fragment ==============
    implementFragmentAndroidKtxAndNavigationCompose()

    // =========== AppCompat ==============
    implementation(Depends.AppCompat.getAppcompat())

    // =========== Material ==============
    implementation(Depends.Material.getMaterial())

    // =========== ViewModel ==============
    implementViewModel()

    // =========== View ==============
    implementLayouts()

    // =========== Robbie ==============
    implementRobbie()

    // =========== Room ==============
    implementRoom()

    // =========== Hilt ==============
    implementHilt()

    // =========== Binding ==============
    implementDatabinding()

    // =========== Retrofit ==============
    implementRetrofit()

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    // =========== Worker ==============
    implementWorker()

    // =========== Lottie ==============
    implementLottie()
}