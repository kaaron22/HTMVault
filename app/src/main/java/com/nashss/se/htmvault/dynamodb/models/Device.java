package com.nashss.se.htmvault.dynamodb.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.LocalDate;

@DynamoDBTable(tableName = "devices")
public class Device {

    private String controlNumber;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private LocalDate manufactureDate;


}
