package pt.ipleiria.mytodo.models

import java.util.*

data class Todo(
    override var id: String = "",
    var text: String = "",
    var date: Date = Date(),
    var by: String= ""
): Base