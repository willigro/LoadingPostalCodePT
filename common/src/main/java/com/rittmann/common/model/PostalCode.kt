package com.rittmann.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rittmann.common.datasource.local.TablePostalCode

// Using the portuguese name to make easier the reading (for me at least)
@Entity(tableName = TablePostalCode.TABLE)
data class PostalCode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TablePostalCode.ID)
    val id: Long,

    @ColumnInfo(name = TablePostalCode.COD_DISTRITO)
    val codDistrito: Int,

    @ColumnInfo(name = TablePostalCode.COD_CONCELHO)
    val codConcelho: Int,

    @ColumnInfo(name = TablePostalCode.COD_LOCALIDADE)
    val codLocalidade: Int,

    @ColumnInfo(name = TablePostalCode.NOME_LOCALIDADE)
    val nameLocalidade: String,

    @ColumnInfo(name = TablePostalCode.COD_ARTERIA)
    val codArteria: Int,

    @ColumnInfo(name = TablePostalCode.TIPO_ARTERIA)
    val tipoArteria: String,

    @ColumnInfo(name = TablePostalCode.PREP_1)
    val prep1: String,

    @ColumnInfo(name = TablePostalCode.TITULO_ARTERIA)
    val tituloArteria: String,

    @ColumnInfo(name = TablePostalCode.PREP_2)
    val prep2: String,

    @ColumnInfo(name = TablePostalCode.NOME_ARTERIA)
    val nomeArteria: String,

    @ColumnInfo(name = TablePostalCode.LOCAL_ARTERIA)
    val localArteria: String,

    @ColumnInfo(name = TablePostalCode.TROCO)
    val troco: String,

    @ColumnInfo(name = TablePostalCode.PORTA)
    val porta: Int,

    @ColumnInfo(name = TablePostalCode.CLIENTE)
    val cliente: String,

    @ColumnInfo(name = TablePostalCode.NUM_COD_POSTAL)
    val numCodPostal: Int,

    @ColumnInfo(name = TablePostalCode.EXT_COD_POSTAL)
    val extCodPostal: Int,

    @ColumnInfo(name = TablePostalCode.DESIG_POSTAL)
    val desgiPostal: String,
) {
    fun retrievePostalCode(): String = "$numCodPostal-$extCodPostal"
}