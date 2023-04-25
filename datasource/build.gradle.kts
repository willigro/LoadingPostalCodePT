import Depends.AndroidTest.implementEspressoTest
import Depends.Hilt.implementHilt
import Depends.Kotlin.implementKotlinForModule
import Depends.Module.implementModules
import Depends.Retrofit.implementRetrofit
import Depends.Robbie.implementRobbie
import Depends.Room.implementRoom
import Depends.Test.implementTest
import Depends.ViewModel.implementViewModel
import Depends.Worker.implementWorker

android {
    buildTypes.forEach {
        it.buildConfigField("String", "BASE_NAME", "\"wtest.db\"")
    }
}

plugins {
    id(Depends.Plugins.HILT)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // =========== Modules ==============
    implementModules(Modules.common)

    // =========== Kotlin ==============
    implementKotlinForModule()

    // =========== AppCompat ==============
    implementation(Depends.AppCompat.getAppcompat())

    // =========== ViewModel ==============
    implementViewModel()

    // =========== Hilt ==============
    implementHilt()

    // =========== Robbie ==============
    implementRobbie()

    // =========== Material ==============
    implementation(Depends.Material.getMaterial())

    // =========== Test ==============
    implementTest()
    implementEspressoTest()
    implementation(Depends.AndroidTest.ESPRESSO_IDLING)
    debugImplementation(Depends.AndroidTest.FRAGMENT_TESTING)

    // =========== Room ==============
    implementRoom()

    // =========== Retrofit ==============
    implementRetrofit()

    // TODO move it to retrofit
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // =========== Worker ==============
    implementWorker()

    implementation ("commons-io:commons-io:2.11.0")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.6.0") //for JVM platform

    implementation("androidx.paging:paging-runtime-ktx:3.0.0")
}