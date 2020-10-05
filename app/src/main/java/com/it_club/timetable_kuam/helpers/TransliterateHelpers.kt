package com.it_club.timetable_kuam.helpers

val specTransTable = mapOf(
    "БЖЗОС" to "BZhZOS",
    "ГМУ" to "GMU",
    "Д" to "D",
    "ДОВ" to "DOV",
    "И" to "I",
    "Инф" to "Inf",
    "ИС" to "IS",
    "ИЯ" to "IJa",
    "КДР" to "KDR",
    "МО" to "MO",
    "НВП" to "NVP",
    "ОПДЭТ" to "OPDET",
    "ПД" to "PD",
    "ПиП" to "PiP",
    "ПМНО" to "PMNO",
    "ПО" to "PO",
    "СПС" to "SPS",
    "Т" to "T",
    "УА" to "UA",
    "Ф" to "F",
    "ФкС" to "FkS",
    "Э" to "E",
    "Ю" to "Ju"
)

val subgroupTransTable = mapOf(
    "ро" to "r",
    "(ро)" to "r",
    "ко" to "k",
    "(ко)" to "k",
    "(1 подгруппа)" to "s1",
    "(2 подгруппа)" to "s2",
    "(архитекторы)" to "a",
    "(графики)" to "g",
    "(промышленники)" to "p"
)

fun renamePostfix(postfix: String): String {
    var (code, subgroup) = postfix.split(" ", limit = 2)
    code = code.replace("сс", "ss")
    subgroup = subgroupTransTable[subgroup].toString()
    return code + subgroup
}

fun transliterateGroupToTopic(groupName: String): String {
    var (pre, post) = groupName.split("-", limit = 2)
    if (post.length > 2)
        post = renamePostfix(post)
    return specTransTable[pre] + post
}