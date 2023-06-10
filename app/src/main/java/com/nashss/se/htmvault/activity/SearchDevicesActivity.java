package com.nashss.se.htmvault.activity;

import com.nashss.se.htmvault.activity.requests.SearchDevicesRequest;
import com.nashss.se.htmvault.activity.results.SearchDevicesResult;
import com.nashss.se.htmvault.converters.ModelConverter;
import com.nashss.se.htmvault.dynamodb.DeviceDao;
import com.nashss.se.htmvault.dynamodb.models.Device;
import com.nashss.se.htmvault.metrics.MetricsPublisher;
import com.nashss.se.htmvault.models.DeviceModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static com.nashss.se.htmvault.utils.NullUtils.ifNull;

public class SearchDevicesActivity {

    private final DeviceDao deviceDao;
    private final MetricsPublisher metricsPublisher;
    private final Logger log = LogManager.getLogger();

    @Inject
    public SearchDevicesActivity(DeviceDao deviceDao, MetricsPublisher metricsPublisher) {
        this.deviceDao = deviceDao;
        this.metricsPublisher = metricsPublisher;
    }

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
