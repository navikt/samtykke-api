package no.nav

import no.nav.database.DataSourceBuilder

class ApplicationContext(private val env: Map<String, String>) {

    init {
        val dataSourceBuilder = DataSourceBuilder(System.getenv())
        dataSourceBuilder.migrate()
        val dataSource = dataSourceBuilder.dataSource
    }
}