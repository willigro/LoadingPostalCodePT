package com.rittmann.common.datasource.local

object QueryDAO {
    const val ASC = " ASC "
    const val DESC = " DESC "

    fun orderBy(col: String, ordenation: String): String {
        return if (col.isEmpty() || ordenation.isEmpty()) "" else " ORDER BY $col $ordenation"
    }

    fun inDate(col: String): String {
        return " strftime('%d'," + col + ") = ? " +
                " AND strftime('%m'," + col + ") = ? " +
                " AND strftime('%Y'," + col + ") =  ? "
    }

    fun betweenDate(dateStart: String, dateEnd: String, col: String): String {
        return " strftime('%Y-%m-%d',$col) BETWEEN '$dateStart' AND '$dateEnd'"
    }

    fun inMonth(col: String): String {
        return " strftime('%m',$col) = ? AND strftime('%Y',$col) =  ? "
    }

}

fun String.selectAll(where: String = ""): String {
    return "SELECT * FROM $this ${where.where()} "
}

fun String.where(): String {
    return if (isEmpty()) "" else " WHERE $this"
}

fun String.orderBy(ordering: String): String {
    return if (isEmpty() || ordering.isEmpty()) "" else " ORDER BY $this $ordering"
}

fun String.groupByThat(): String {
    return if (isEmpty()) "" else " GROUP BY $this"
}

fun String.like(like: String): String {
    return if (like.isEmpty()) "" else " $this LIKE '%$like%' "
}