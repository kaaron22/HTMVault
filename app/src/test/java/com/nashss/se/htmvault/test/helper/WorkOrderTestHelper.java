package com.nashss.se.htmvault.test.helper;

import com.nashss.se.htmvault.dynamodb.models.ManufacturerModel;
import com.nashss.se.htmvault.dynamodb.models.WorkOrder;
import com.nashss.se.htmvault.models.WorkOrderCompletionStatus;
import com.nashss.se.htmvault.models.WorkOrderModel;
import com.nashss.se.htmvault.models.WorkOrderType;
import com.nashss.se.htmvault.utils.HTMVaultServiceUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class WorkOrderTestHelper {

    private final Map<Integer, WorkOrderType> workOrderTypeOptions;
    private final Map<Integer, WorkOrderCompletionStatus> workOrderCompletionStatusOptions;

    private WorkOrderTestHelper() {
        workOrderTypeOptions = new HashMap<>();
        int optionNumber = 0;
        for (WorkOrderType workOrderType : WorkOrderType.values()) {
            workOrderTypeOptions.put(optionNumber, workOrderType);
            optionNumber++;
        }

        workOrderCompletionStatusOptions = new HashMap<>();
        optionNumber = 0;
        for (WorkOrderCompletionStatus workOrderCompletionStatus : WorkOrderCompletionStatus.values()) {
            workOrderCompletionStatusOptions.put(optionNumber, workOrderCompletionStatus);
            optionNumber++;
        }
    }

    public WorkOrder generateWorkOrder(int sequenceNumber, String identifyingNumber,
                                       ManufacturerModel manufacturerModel, String facilityName,
                                       String assignedDepartment) {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId("WR" + sequenceNumber);
        workOrder.setWorkOrderType(workOrderTypeOptions.get(generateRandomIntWithLimit(workOrderTypeOptions.size())));
        workOrder.setControlNumber("CN" + identifyingNumber);
        workOrder.setSerialNumber("SN" + identifyingNumber);
        workOrder.setWorkOrderCompletionStatus(workOrderCompletionStatusOptions
                .get(generateRandomIntWithLimit(workOrderCompletionStatusOptions.size())));
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

    public static int generateRandomIntWithLimit(int exclusiveLimit) {
        Random random = new Random();
        return random.nextInt(exclusiveLimit);
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
        assertEquals(null == workOrder.getWorkOrderAwaitStatus() ? "" : workOrder.getWorkOrderAwaitStatus(),
                workOrderModel.getWorkOrderAwaitStatus(), message);
        assertEquals(workOrder.getManufacturerModel().getManufacturer(), workOrderModel.getManufacturer(), message);
        assertEquals(workOrder.getManufacturerModel().getManufacturer(), workOrderModel.getModel(), message);
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
