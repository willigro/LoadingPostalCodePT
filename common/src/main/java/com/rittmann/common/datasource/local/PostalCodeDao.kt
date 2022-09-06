package com.rittmann.common.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.rittmann.common.model.PostalCode
import java.util.*

@Dao
interface PostalCodeDao {

    @Query("SELECT COUNT(${TablePostalCode.ID}) FROM ${TablePostalCode.TABLE}")
    fun getCount(): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postalCodes: List<PostalCode>)

    /* SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade REGEXP   ORDER BY id_postal_code ASC LIMIT 100 OFFSET 0
SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade REGEXP  '.*(?=.*(?i)j)(?=.*(?i)d)(?=.*(?i)h)(?=.*(?i)t)(?=.*(?i)S)(?=.*(?i)a)(?=.*(?i)o).*'  ORDER BY nome_localidade ASC
    '.*(?=.*(?i)S)(?=.*(?i)ã)(?=.*(?i)o).*' -> São
    Case Insensitive '.*(?=.*(?i)D).*'
    Accent '.*(?=.*ã).*'
SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade LIKE '%sao %' AND nome_localidade LIKE '% joA %' AND nome_localidade LIKE '% da %' AND nome_localidade LIKE '% TaLH%' ORDER BY nome_localidade ASC

It works without accent
SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade LIKE '%s%' AND nome_localidade LIKE '%jo%' AND nome_localidade LIKE '%da%' AND nome_localidade LIKE '%TaLH%' ORDER BY nome_localidade ASC

    "sAo joA da TaLH '.*(?=.*s-S).*'
    SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade REGEXP  '.*(?=.*S)(?=.*A)(?=.*O).*'  ORDER BY id_postal_code ASC LIMIT 100 OFFSET 0
    SELECT nome_localidade FROM tb_postal_code WHERE  nome_localidade REGEXP  '.*[talh joa].*'  ORDER BY id_postal_code ASC LIMIT 100 OFFSET 0
     SELECT * FROM tb_postal_code WHERE num_cod_postal LIKE '%3030%' OR ext_cod_postal LIKE '%181%'  OR nome_localidade LIKE '%Co%' ORDER BY id_postal_code ASC LIMIT 10 OFFSET 0
          SELECT * FROM tb_postal_code WHERE num_cod_postal LIKE '%2695%' AND ext_cod_postal LIKE '%650%'  AND nome_localidade LIKE '%t%' ORDER BY id_postal_code ASC LIMIT 10 OFFSET 0



        USE IT
        SELECT nome_localidade FROM tb_postal_code WHERE  (nome_localidade LIKE '%sa%' OR nome_localidade LIKE '%sã%') AND nome_localidade LIKE '%jo%' AND nome_localidade LIKE '%da%' AND nome_localidade LIKE '%TaLH%' ORDER BY nome_localidade ASC
        Try to combine regex and like
     */

    @RawQuery(observedEntities = [PostalCode::class])
    fun getPagedList(query: SupportSQLiteQuery): List<PostalCode>
}

