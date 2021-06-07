package pt.ipleiria.mytodo.models

data class Group(
        override var key: String = "",
        var owner: String = "",
        var name: String = "",
        var members: List<String> = listOf(),
        var todos: List<Todo> = listOf()
): Base