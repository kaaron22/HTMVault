package com.nashss.se.htmvault.metrics;

/**
 * Constant values for use with metrics.
 */
public class MetricsConstants {
    public static final String GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT =
            "GetManufacturerModel.ManufacturerModelNotFoundException.Count";
    public static final String GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT =
            "GetFacilityDepartment.FacilityDepartmentNotFoundException.Count";
    public static final String ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT =
            "AddDevice.InvalidAttributeValueException.Count";
    public static final String GETDEVICE_DEVICENOTFOUND_COUNT = "GetDevice.DeviceNotFoundException.Count";
    public static final String GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT =
            "GetDeviceWorkOrders.InvalidAttributeValueException.Count";
    public static final String CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT =
            "CreateWorkOrder.InvalidAttributeValueException.Count";
    public static final String GETWORKORDER_WORKORDERNOTFOUND_COUNT = "GetWorkOrder.WorkOrderNotFoundException.Count";

    public static final String UPDATEPLAYLIST_INVALIDATTRIBUTEVALUE_COUNT =
        "UpdatePlaylist.InvalidAttributeValueException.Count";
    public static final String UPDATEPLAYLIST_INVALIDATTRIBUTECHANGE_COUNT =
        "UpdatePlaylist.InvalidAttributeChangeException.Count";
    public static final String SERVICE = "Service";
    public static final String SERVICE_NAME = "HTMVault";
    public static final String NAMESPACE_NAME = "U7-Capstone/HTMVault";
}
