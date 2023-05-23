package com.nashss.se.htmvault.dependency;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nashss.se.htmvault.dynamodb.DynamoDbClientProvider;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class DaoModule {

    @Singleton
    @Provides
    public DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_EAST_2));
    }
}
