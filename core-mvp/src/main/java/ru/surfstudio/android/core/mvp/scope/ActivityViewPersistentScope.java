/*
  Copyright (c) 2018-present, SurfStudio LLC, Maxim Tuev.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package ru.surfstudio.android.core.mvp.scope;

import ru.surfstudio.android.core.mvp.configurator.BaseActivityViewConfigurator;
import ru.surfstudio.android.core.mvp.state.ActivityViewScreenState;
import ru.surfstudio.android.core.ui.event.ActivityScreenEventDelegateManager;
import ru.surfstudio.android.core.ui.scope.ActivityPersistentScope;
import ru.surfstudio.android.core.ui.scope.PersistentScope;

/**
 * {@link PersistentScope} для активити
 */
public final class ActivityViewPersistentScope extends ActivityPersistentScope {

    public ActivityViewPersistentScope(
            ActivityScreenEventDelegateManager screenEventDelegateManager,
            ActivityViewScreenState screenState,
            BaseActivityViewConfigurator configurator,
            String scopeId) {
        super(screenEventDelegateManager, screenState, configurator, scopeId);
    }

    @Override
    public BaseActivityViewConfigurator getConfigurator() {
        return (BaseActivityViewConfigurator) super.getConfigurator();
    }

    @Override
    public ActivityViewScreenState getScreenState() {
        return (ActivityViewScreenState) super.getScreenState();
    }
}
