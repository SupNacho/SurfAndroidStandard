package ru.surfstudio.android.core.ui.base.screen.view.core;

import android.app.Activity;
import android.os.Bundle;

import ru.surfstudio.android.core.ui.base.screen.configurator.ScreenConfigurator;

/**
 * инрефейс для вью, которая оповещает презентер о событиях жизненного цикла экрана
 */
public interface PresenterHolderFragmentCoreView extends PresenterHolderCoreView {

    ScreenConfigurator createScreenConfigurator(Activity activity, Bundle bundle);

    /**
     * @return fragment arguments
     */
    Bundle getStartArgs();

    /**
     * @param viewRecreated show whether view created in first time or recreated after
     *                      changing configuration
     */
    void onActivityCreated(Bundle savedInstanceState, boolean viewRecreated);

}