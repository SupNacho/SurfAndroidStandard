package ru.surfstudio.android.utilktx.data.wrapper.blockable

import ru.surfstudio.android.utilktx.data.wrapper.DataWrapperInterface

/**
 * Интерфейс сущности, если может блокировать своё состояние
 */
interface BlockableDataInterface {

    var isBlocked: Boolean

    fun block() {
        isBlocked = true
    }

    fun unblock() {
        isBlocked = false
    }
}

class BlockableData<T>(override var data: T)
    : DataWrapperInterface<T>, BlockableDataInterface {

    override var isBlocked: Boolean = false
}