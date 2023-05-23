package com.nashss.se.htmvault.dependency;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DaoModule.class, MetricsModule.class})
public interface ServiceComponent {

    AddDeviceActivity provideAddDeviceActivity();
}
