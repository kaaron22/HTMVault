package com.nashss.se.htmvault.dependency;

import com.nashss.se.htmvault.activity.AddDeviceActivity;
import com.nashss.se.htmvault.activity.GetDeviceActivity;
import com.nashss.se.htmvault.activity.GetDeviceWorkOrdersActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DaoModule.class, MetricsModule.class})
public interface ServiceComponent {

    AddDeviceActivity provideAddDeviceActivity();

    GetDeviceActivity provideGetDeviceActivity();

    GetDeviceWorkOrdersActivity provideGetDeviceWorkOrdersActivity();
}
