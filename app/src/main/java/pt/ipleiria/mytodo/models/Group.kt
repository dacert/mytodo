package pt.ipleiria.mytodo.models

import java.util.*

data class Group(
        override var id: String = "",
        var owner: String = "",
        var name: String = "",
        var members: List<String> = listOf(),
        override var timestamp: Date? = Date()
): Base