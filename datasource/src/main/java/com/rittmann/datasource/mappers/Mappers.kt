package com.rittmann.datasource.mappers

import com.rittmann.common.extensions.toIntOrZero
import com.rittmann.datasource.model.PostalCode

fun List<String>.lineStringFromCsvToPostalCodeList(): PostalCode =
    PostalCode(
        id = 0L,
        codDistrito = this[0].toIntOrZero(),
        codConcelho = this[1].toIntOrZero(),
        codLocalidade = this[2].toIntOrZero(),
        nameLocalidade = this[3],
        codArteria = this[4].toIntOrZero(),
        tipoArteria = this[5],
        prep1 = this[6],
        tituloArteria = this[7],
        prep2 = this[8],
        nomeArteria = this[9],
        localArteria = this[10],
        troco = this[11],
        porta = this[12].toIntOrZero(),
        cliente = this[13],
        numCodPostal = this[14].toIntOrZero(),
        extCodPostal = this[15].toIntOrZero(),
        desgiPostal = this[16],
    )