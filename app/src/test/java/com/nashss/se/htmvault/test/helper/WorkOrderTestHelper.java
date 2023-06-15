package com.nashss.se.htmvault.test.helper;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.nashss.se.htmvault.utils.HTMVaultServiceUtils.generateRandomIntWithLimit;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class WorkOrderTestHelper {


    private WorkOrderTestHelper() {
    }

    public static WorkOrder generateWorkOrder(int sequenceNumber, String controlNumber, String serialNumber,
                                       ManufacturerModel manufacturerModel, String facilityName,
                                       String assignedDepartment) {

        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(HTMVaultServiceUtils.generateId("WR",6));
        workOrder.setWorkOrderType(getRandomEnumValue(WorkOrderType.values()));
        workOrder.setControlNumber(controlNumber);
        workOrder.setSerialNumber(serialNumber);
        workOrder.setWorkOrderCompletionStatus(getRandomEnumValue(WorkOrderCompletionStatus.values()));
        workOrder.setManufacturerModel(manufacturerModel);
        workOrder.setFacilityName(facilityName);
        workOrder.setAssignedDepartment(assignedDepartment);
        workOrder.setProblemReported("Problem " + sequenceNumber);
        workOrder.setCreatedById(String.valueOf(sequenceNumber));
        workOrder.setCreatedByName(String.valueOf(sequenceNumber));
        workOrder.setCreationDateTime(LocalDateTime.now().minusYears(generateRandomIntWithLimit(3))
                .minusDays(generateRandomIntWithLimit(365)));

        return workOrder;
    }

    private static <T> T getRandomEnumValue(T[] values) {
        int randomIndex = generateRandomIntWithLimit(values.length);
        return values[randomIndex];
    }

    public static void assertWorkOrdersEqualWorkOrderModels(List<WorkOrder> workOrders,
                                                            List<WorkOrderModel> workOrderModels) {
        assertEquals(workOrders.size(),
                     workOrderModels.size(),
                     String.format("Expected work orders (%s) and work order models (%s) to match",
                                   workOrders,
                                   workOrderModels));
        for (int i = 0; i < workOrders.size(); i++) {
            assertWorkOrderEqualsWorkOrderModel(
                workOrders.get(i),
                workOrderModels.get(i),
                String.format("Expected %dth work order (%s) to match corresponding work order model (%s)",
                              i,
                              workOrders.get(i),
                              workOrderModels.get(i)));
        }
    }

    public static void assertWorkOrderEqualsWorkOrderModel(WorkOrder workOrder, WorkOrderModel workOrderModel) {
        String message = String.format("Expected work order %s to match work order model %s", workOrder,
                workOrderModel);
        assertWorkOrderEqualsWorkOrderModel(workOrder, workOrderModel, message);
    }

    public static void assertWorkOrderEqualsWorkOrderModel(WorkOrder workOrder, WorkOrderModel workOrderModel,
                                                           String message) {
        assertEquals(workOrder.getWorkOrderId(), workOrderModel.getWorkOrderId(), message);
        assertEquals(workOrder.getWorkOrderType().toString(), workOrderModel.getWorkOrderType(), message);
        assertEquals(workOrder.getControlNumber(), workOrderModel.getControlNumber(), message);
        assertEquals(workOrder.getSerialNumber(), workOrderModel.getSerialNumber(), message);
        assertEquals(workOrder.getWorkOrderCompletionStatus().toString(), workOrderModel.getWorkOrderCompletionStatus(),
                message);
        assertEquals(null == workOrder.getWorkOrderAwaitStatus() ? "" : workOrder.getWorkOrderAwaitStatus().toString(),
                workOrderModel.getWorkOrderAwaitStatus(), message);
        assertEquals(workOrder.getManufacturerModel().getManufacturer(), workOrderModel.getManufacturer(), message);
        assertEquals(workOrder.getManufacturerModel().getModel(), workOrderModel.getModel(), message);
        assertEquals(workOrder.getFacilityName(), workOrderModel.getFacilityName(), message);
        assertEquals(workOrder.getAssignedDepartment(), workOrderModel.getAssignedDepartment(), message);
        assertEquals(workOrder.getProblemReported(), workOrderModel.getProblemReported(), message);
        assertEquals(null == workOrder.getProblemFound() ? "" : workOrder.getProblemFound(),
                workOrderModel.getProblemFound(), message);
        assertEquals(workOrder.getCreatedById(), workOrderModel.getCreatedById(), message);
        assertEquals(workOrder.getCreatedByName(), workOrderModel.getCreatedByName(), message);
        assertEquals(HTMVaultServiceUtils.formatLocalDateTime(workOrder.getCreationDateTime()),
                workOrderModel.getCreationDateTime(), message);
        assertEquals(null == workOrder.getClosedById() ? "" : workOrder.getClosedById(), workOrderModel.getClosedById(),
                message);
        assertEquals(null == workOrder.getClosedByName() ? "" : workOrder.getClosedByName(),
                workOrderModel.getClosedByName(), message);
        assertEquals(null == workOrder.getClosedDateTime() ? "" :
                HTMVaultServiceUtils.formatLocalDateTime(workOrder.getClosedDateTime()),
                workOrderModel.getClosedDateTime(), message);
        assertEquals(null == workOrder.getSummary() ? "" : workOrder.getSummary(), workOrderModel.getSummary(),
                message);
        assertEquals(null == workOrder.getCompletionDateTime() ? "" :
                        HTMVaultServiceUtils.formatLocalDateTime(workOrder.getCompletionDateTime()),
                workOrderModel.getCompletionDateTime(), message);
    }
}
