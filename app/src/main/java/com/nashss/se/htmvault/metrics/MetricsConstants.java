package com.nashss.se.htmvault.metrics;

// initial from project template; additional constants added
/**
 * Constant values for use with metrics.
 */
public class MetricsConstants {
    public static final String ADDDEVICE_INVALIDATTRIBUTEVALUE_COUNT =
            "AddDevice.InvalidAttributeValueException.Count";
    public static final String CLOSEWORKORDER_DEVICENOTFOUND_COUNT =
            "CloseWorkOrder.DeviceNotFoundException.Count";
    public static final String CLOSEWORKORDER_WORKORDERNOTCOMPLETE_COUNT =
            "CloseWorkOrder.CloseWorkOrderNotCompleteException.Count";
    public static final String CLOSEWORKORDER_WORKORDERNOTFOUND_COUNT =
            "CloseWorkOrder.WorkOrderNotFoundException.Count";
    public static final String CREATEWORKORDER_DEVICENOTFOUND_COUNT =
            "CreateWorkOrder.DeviceNotFoundException.Count";
    public static final String CREATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT =
            "CreateWorkOrder.InvalidAttributeValueException.Count";
    public static final String GETDEVICE_DEVICENOTFOUND_COUNT =
            "GetDevice.DeviceNotFoundException.Count";
    public static final String GETDEVICEWORKORDERS_INVALIDATTRIBUTEVALUE_COUNT =
            "GetDeviceWorkOrders.InvalidAttributeValueException.Count";
    public static final String GETFACILITYDEPARTMENT_FACILITYDEPARTMENTNOTFOUND_COUNT =
            "GetFacilityDepartment.FacilityDepartmentNotFoundException.Count";
    public static final String GETMANUFACTURERMODEL_MANUFACTURERMODELNOTFOUND_COUNT =
            "GetManufacturerModel.ManufacturerModelNotFoundException.Count";
    public static final String GETWORKORDER_WORKORDERNOTFOUND_COUNT =
            "GetWorkOrder.WorkOrderNotFoundException.Count";
    public static final String REACTIVATEDEVICE_DEVICENOTFOUND_COUNT =
            "ReactivateDevice.DeviceNotFoundException.Count";
    public static final String RETIREDEVICE_DEVICENOTFOUND_COUNT =
            "RetireDevice.DeviceNotFoundException.Count";
    public static final String RETIREDEVICE_WORKORDERSOPEN_COUNT =
            "RetireDevice.RetireDeviceWithOpenWorkOrdersException.Count";
    public static final String UPDATEDEVICE_DEVICENOTFOUND_COUNT =
            "UpdateDevice.DeviceNotFoundException.Count";
    public static final String UPDATEDEVICE_DEVICERETIRED_COUNT =
            "UpdateDevice.UpdateDeviceRetiredException.Count";
    public static final String UPDATEDEVICE_INVALIDATTRIBUTEVALUE_COUNT =
            "UpdateDevice.InvalidAttributeValueException.Count";
    public static final String UPDATEWORKORDER_INVALIDATTRIBUTEVALUE_COUNT =
            "UpdateWorkOrder.InvalidAttributeValueException.Count";
    public static final String UPDATEWORKORDER_WORKORDERCLOSED_COUNT =
            "UpdateWorkOrder.WorkOrderClosedException.Count";
    public static final String UPDATEWORKORDER_WORKORDERNOTFOUND_COUNT =
            "UpdateWorkOrder.WorkOrderNotFoundException.Count";

    public static final String SERVICE = "Service";
    public static final String SERVICE_NAME = "HTMVault";
    public static final String NAMESPACE_NAME = "U7-Capstone/HTMVault";
}
