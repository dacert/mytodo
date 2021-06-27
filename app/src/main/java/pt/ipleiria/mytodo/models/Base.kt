package pt.ipleiria.mytodo.models

import java.io.Serializable
import java.util.*

interface Base: Serializable { val id: String; val timestamp: Date? }