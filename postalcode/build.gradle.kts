import Depends.AndroidTest.implementEspressoTest
import Depends.Hilt.implementHilt
import Depends.Databinding.implementDatabinding
import Depends.Fragment.implementFragmentKtx
import Depends.Kotlin.implementKotlinForModule
import Depends.Module.implementModules
import Depends.Retrofit.implementRetrofit
import Depends.Robbie.implementRobbie
import Depends.Room.implementRoom
import Depends.Test.implementTest
import Depends.ViewModel.implementViewModel
import Depends.Worker.implementWorker

plugins {
    id("org.jetbrains.kotlin.android")
    id(Depends.Plugins.HILT)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.paging:paging-runtime-ktx:3.0.0")

    // =========== Modules ==============
    implementModules(Modules.common)

    // =========== Kotlin ==============
    implementKotlinForModule()

    // =========== Fragment ==============
    implementFragmentKtx()

    // =========== AppCompat ==============
    implementation(Depends.AppCompat.getAppcompat())

    // =========== ViewModel ==============
    implementViewModel()

    // =========== Dagger ==============
    implementHilt()

    // =========== Robbie ==============
    implementRobbie()

    // =========== Material ==============
    implementation(Depends.Material.getMaterial())

    // =========== Test ==============
    implementTest()
    implementEspressoTest()
    // TODO move it
    implementation(Depends.AndroidTest.ESPRESSO_IDLING)
    debugImplementation(Depends.AndroidTest.FRAGMENT_TESTING)

    // =========== Room ==============
    implementRoom()

    // =========== Retrofit ==============
    implementRetrofit()

    // TODO move it
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // =========== DataBinding ==============
    implementDatabinding()

    // =========== Worker ==============
    implementWorker()
}