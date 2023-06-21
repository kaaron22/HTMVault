package com.nashss.se.htmvault.dependency;

import com.nashss.se.htmvault.activity.AddDeviceActivity;
import com.nashss.se.htmvault.activity.CloseWorkOrderActivity;
import com.nashss.se.htmvault.activity.CreateWorkOrderActivity;
import com.nashss.se.htmvault.activity.GetDeviceActivity;
import com.nashss.se.htmvault.activity.GetDeviceWorkOrdersActivity;
import com.nashss.se.htmvault.activity.GetFacilitiesAndDepartmentsActivity;
import com.nashss.se.htmvault.activity.GetManufacturersAndModelsActivity;
import com.nashss.se.htmvault.activity.GetWorkOrderActivity;
import com.nashss.se.htmvault.activity.ReactivateDeviceActivity;
import com.nashss.se.htmvault.activity.RetireDeviceActivity;
import com.nashss.se.htmvault.activity.SearchDevicesActivity;
import com.nashss.se.htmvault.activity.UpdateDeviceActivity;
import com.nashss.se.htmvault.activity.UpdateWorkOrderActivity;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DaoModule.class, MetricsModule.class})
public interface ServiceComponent {

    /**
     * Provides add device activity.
     *
     * @return the add device activity
     */
    AddDeviceActivity provideAddDeviceActivity();

    /**
     * Provides get device activity.
     *
     * @return the get device activity
     */
    GetDeviceActivity provideGetDeviceActivity();

    /**
     * Provides get device work orders activity.
     *
     * @return the get device work orders activity
     */
    GetDeviceWorkOrdersActivity provideGetDeviceWorkOrdersActivity();

    /**
     * Provides retire device activity.
     *
     * @return the retire device activity
     */
    RetireDeviceActivity provideRetireDeviceActivity();

    /**
     * Provides update device activity.
     *
     * @return the update device activity
     */
    UpdateDeviceActivity provideUpdateDeviceActivity();

    /**
     * Provides ssearch devices activity.
     *
     * @return the search devices activity
     */
    SearchDevicesActivity provideSearchDevicesActivity();

    /**
     * Provides reactivate device activity.
     *
     * @return the reactivate device activity
     */
    ReactivateDeviceActivity provideReactivateDeviceActivity();

    /**
     * Provides create work order activity.
     *
     * @return the create work order activity
     */
    CreateWorkOrderActivity provideCreateWorkOrderActivity();

    /**
     * Provides get work order activity.
     *
     * @return the get work order activity
     */
    GetWorkOrderActivity provideGetWorkOrderActivity();

    /**
     * Provides update work order activity.
     *
     * @return the update work order activity
     */
    UpdateWorkOrderActivity provideUpdateWorkOrderActivity();

    /**
     * Provides close work order activity.
     *
     * @return the close work order activity
     */
    CloseWorkOrderActivity provideCloseWorkOrderActivity();

    /**
     * Provides get manufacturers and models activity get manufacturers and models activity.
     *
     * @return the get manufacturers and models activity
     */
    GetManufacturersAndModelsActivity provideGetManufacturersAndModelsActivity();

    /**
     * Provides get facilities and departments activity.
     *
     * @return the get facilities and departments activity
     */
    GetFacilitiesAndDepartmentsActivity provideGetFacilitiesAndDepartmentsActivity();
}
