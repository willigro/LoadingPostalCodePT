package com.rittmann.common.mappers

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test

class MappersKtTest {

    @Test
    fun `split the postal code line from a CSV, expecting that always returns the same amount`() {
        val expected = 17
        val stub1 = "01,01,249,Alcafaz,,,,,,,,,,,3750,011,AGADÃO".split(",")
        val stub2 =
            "01,01,283,Póvoa do Vale do Trigo,15315100,Beco,das,,,Flores,,,,,3750,364,BELAZAIMA DO CHÃO".split(
                ","
            )
        val stub3 = "01,01,289,Águeda,21810101,Rua,do,,,Ameal,Ameal,,,,3750,303,ÁGUEDA".split(",")
        assertThat(stub1.size, `is`(expected))
        assertThat(stub2.size, `is`(expected))
        assertThat(stub3.size, `is`(expected))
    }
}