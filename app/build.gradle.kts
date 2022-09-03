import Depends.AndroidTest.implementAndroidTest
import Depends.AndroidTest.implementEspressoTest
import Depends.Dagger.implementDagger
import Depends.Databinding.implementDatabinding
import Depends.Kotlin.implementKotlinForModule
import Depends.Module.implementAllModules
import Depends.Retrofit.implementRetrofit
import Depends.Robbie.implementRobbie
import Depends.Room.implementRoom
import Depends.Test.implementTest
import Depends.Views.implementLayouts
import Depends.Worker.implementWorker

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // =========== Modules ==============
    implementAllModules(Modules.app)

    // =========== Kotlin ==============
    implementKotlinForModule()

    // =========== AppCompat ==============
    implementation(Depends.AppCompat.getAppcompat())

    // =========== Material ==============
    implementation(Depends.Material.getMaterial())

    // =========== View ==============
    implementLayouts()

    // =========== Test ==============
    implementTest()
    implementEspressoTest()
    implementAndroidTest()
    configurations.all {
        resolutionStrategy {
            force("androidx.test:monitor:1.4.0")
        }
    }

    // =========== Robbie ==============
    implementRobbie()

    // =========== Room ==============
    implementRoom()

    // =========== Dagger ==============
    implementDagger()

    // =========== Binding ==============
    implementDatabinding()

    // =========== Retrofit ==============
    implementRetrofit()

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    // =========== Worker ==============
    implementWorker()
}