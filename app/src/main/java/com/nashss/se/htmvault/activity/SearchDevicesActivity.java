package com.nashss.se.htmvault.activity;

// from project template, modified for project
import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.inject.Inject;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;

public class SearchDevicesActivity {

    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    /**
     * Instantiates a new Search devices activity.
     *
     * @param deviceDao        the device dao
     * @param metricsPublisher the metrics publisher
     */
    @Inject
    public SearchDevicesActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

    /**
     * Handles a request to scan for devices matching a set of criteria (i.e. matching a model name and/or a
     * serial number, etc.)
     *
     * @param searchDevicesRequest the search devices request
     * @return the search devices result
     */
    public SearchDevicesResult handleRequest(final SearchDevicesRequest searchDevicesRequest) {
        log.info("Received SearchPlaylistsRequest {}", searchDevicesRequest);

        String criteria = ifNull(searchDevicesRequest.getCriteria(), "");
        String[] criteriaArray = criteria.isBlank() ? new String[0] : criteria.split("\\s");

        List<Device> results = deviceDao.searchDevices(criteriaArray);
        List<DeviceModel> deviceModels = new ModelConverter().toDeviceModelList(results);

        return SearchDevicesResult.builder()
                .withDevices(deviceModels)
                .build();
    }
}
