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

/**
 * The interface Service component.
 */
@Singleton
@Component(modules = {DaoModule.class, MetricsModule.class})
public interface ServiceComponent {

    /**
     * Provide add device activity add device activity.
     *
     * @return the add device activity
     */
    AddDeviceActivity provideAddDeviceActivity();

    /**
     * Provide get device activity get device activity.
     *
     * @return the get device activity
     */
    GetDeviceActivity provideGetDeviceActivity();

    /**
     * Provide get device work orders activity get device work orders activity.
     *
     * @return the get device work orders activity
     */
    GetDeviceWorkOrdersActivity provideGetDeviceWorkOrdersActivity();

    /**
     * Provide retire device activity retire device activity.
     *
     * @return the retire device activity
     */
    RetireDeviceActivity provideRetireDeviceActivity();

    /**
     * Provide update device activity update device activity.
     *
     * @return the update device activity
     */
    UpdateDeviceActivity provideUpdateDeviceActivity();

    /**
     * Provide search devices activity search devices activity.
     *
     * @return the search devices activity
     */
    SearchDevicesActivity provideSearchDevicesActivity();

    /**
     * Provide reactivate device activity reactivate device activity.
     *
     * @return the reactivate device activity
     */
    ReactivateDeviceActivity provideReactivateDeviceActivity();

    /**
     * Provide create work order activity create work order activity.
     *
     * @return the create work order activity
     */
    CreateWorkOrderActivity provideCreateWorkOrderActivity();

    /**
     * Provide get work order activity get work order activity.
     *
     * @return the get work order activity
     */
    GetWorkOrderActivity provideGetWorkOrderActivity();

    /**
     * Provide update work order activity update work order activity.
     *
     * @return the update work order activity
     */
    UpdateWorkOrderActivity provideUpdateWorkOrderActivity();

    /**
     * Provide close work order activity close work order activity.
     *
     * @return the close work order activity
     */
    CloseWorkOrderActivity provideCloseWorkOrderActivity();

    /**
     * Provide get manufacturers and models activity get manufacturers and models activity.
     *
     * @return the get manufacturers and models activity
     */
    GetManufacturersAndModelsActivity provideGetManufacturersAndModelsActivity();

    /**
     * Provide get facilities and departments activity get facilities and departments activity.
     *
     * @return the get facilities and departments activity
     */
    GetFacilitiesAndDepartmentsActivity provideGetFacilitiesAndDepartmentsActivity();
}
