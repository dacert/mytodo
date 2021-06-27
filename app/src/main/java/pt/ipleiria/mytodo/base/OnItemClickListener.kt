package pt.ipleiria.mytodo.base

import pt.ipleiria.mytodo.models.Base

interface OnItemClickListener {
    fun onClick(item: Base)
}