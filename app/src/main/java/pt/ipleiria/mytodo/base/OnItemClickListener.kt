package pt.ipleiria.mytodo.base

import pt.ipleiria.mytodo.dataLayer.models.Base

interface OnItemClickListener {
    fun onClick(item: Base)
}