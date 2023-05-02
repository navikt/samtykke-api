package no.nav

fun createRandomString(length: Int): String {
    return (1..length).map {
        ('A'..'Z').random()
    }.joinToString("")
}