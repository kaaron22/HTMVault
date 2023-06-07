package com.nashss.se.htmvault.converters;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nashss.se.htmvault.dynamodb.models.WorkOrderSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class WorkOrderSummaryListConverter implements DynamoDBTypeConverter<String, List<WorkOrderSummary>> {

    private static final Gson GSON = new Gson();
    private final Logger log = LogManager.getLogger();

    @Override
    public String convert(List<WorkOrderSummary> workOrderSummariesToSerialize) {
        return GSON.toJson(workOrderSummariesToSerialize);
    }

    @Override
    public List<WorkOrderSummary> unconvert(String serializedListWorkOrderSummaries) {
        return GSON.fromJson(serializedListWorkOrderSummaries, new TypeToken<List<WorkOrderSummary>>() { } .getType());
    }
}
