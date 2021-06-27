package pt.ipleiria.mytodo.models

import java.util.*

data class Todo(
    override var id: String = "",
    var text: String = "",
    var by: String= "",
    override var timestamp: Date? = Date(),
): Base