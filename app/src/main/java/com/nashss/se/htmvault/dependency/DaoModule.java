package com.nashss.se.htmvault.dependency;

import com.nashss.se.htmvault.dynamodb.DynamoDbClientProvider;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

// from project template
/**
 * Dagger Module providing dependency for dao classes.
 */
@Module
public class DaoModule {

    /**
     * Provides a DynamoDBMapper
     *
     * @return the DynamoDBMapper
     */
    @Singleton
    @Provides
    public DynamoDBMapper provideDynamoDBMapper() {
        return new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_EAST_2));
    }
}
