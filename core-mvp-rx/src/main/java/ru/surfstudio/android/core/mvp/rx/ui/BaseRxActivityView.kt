/*
 * Copyright (c) 2019-present, SurfStudio LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.surfstudio.android.core.mvp.rx.ui

import androidx.annotation.CallSuper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import ru.surfstudio.android.core.mvp.activity.CoreActivityView
import ru.surfstudio.android.core.mvp.fragment.CoreFragmentView
import ru.surfstudio.android.mvp.dialog.complex.CoreDialogFragmentView

/**
 * Базовая Activity для связывания с моделью
 */
abstract class BaseRxActivityView<M : ViewBinding> : CoreActivityView(), BindableRxView<M> {

    private val viewDisposable = CompositeDisposable()

    @CallSuper
    override fun onDestroy() {
        viewDisposable.clear()
        super.onDestroy()
    }

    override fun <T> subscribe(observable: Observable<T>, onNext: Consumer<T>, onError: (Throwable) -> Unit): Disposable =
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext, Consumer(onError))
                    .also { viewDisposable.add(it) }
}

/**
 * Базовый Fragment для связывания с моделью
 */
abstract class BaseRxFragmentView<M : ViewBinding> : CoreFragmentView(), BindableRxView<M> {

    private val viewDisposable = CompositeDisposable()

    @CallSuper
    override fun onDestroy() {
        viewDisposable.clear()
        super.onDestroy()
    }

    override fun <T> subscribe(observable: Observable<T>, onNext: Consumer<T>, onError: (Throwable) -> Unit): Disposable =
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext, Consumer(onError))
                    .also { viewDisposable.add(it) }
}

abstract class BaseRxDialogView<M : ViewBinding> : CoreDialogFragmentView(), BindableRxView<M> {

    private val viewDisposable = CompositeDisposable()

    @CallSuper
    override fun onDestroy() {
        viewDisposable.clear()
        super.onDestroy()
    }

    override fun <T> subscribe(observable: Observable<T>, onNext: Consumer<T>, onError: (Throwable) -> Unit): Disposable =
            observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onNext, Consumer(onError))
                    .also { viewDisposable.add(it) }
}