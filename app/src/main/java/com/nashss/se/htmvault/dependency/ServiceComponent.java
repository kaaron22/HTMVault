package com.nashss.se.htmvault.dependency;

import com.nashss.se.htmvault.activity.*;
import com.nashss.se.htmvault.activity.results.CloseWorkOrderResult;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DaoModule.class, MetricsModule.class})
public interface ServiceComponent {

    AddDeviceActivity provideAddDeviceActivity();

    GetDeviceActivity provideGetDeviceActivity();

    GetDeviceWorkOrdersActivity provideGetDeviceWorkOrdersActivity();

    RetireDeviceActivity provideRetireDeviceActivity();

    UpdateDeviceActivity provideUpdateDeviceActivity();

    SearchDevicesActivity provideSearchDevicesActivity();

    ReactivateDeviceActivity provideReactivateDeviceActivity();

    CreateWorkOrderActivity provideCreateWorkOrderActivity();

    GetWorkOrderActivity provideGetWorkOrderActivity();

    UpdateWorkOrderActivity provideUpdateWorkOrderActivity();

    CloseWorkOrderActivity provideCloseWorkOrderActivity();

    GetManufacturersAndModelsActivity provideGetManufacturersAndModelsActivity();
}
