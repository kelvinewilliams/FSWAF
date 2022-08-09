package dev.k77.fluentstream.utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.wafv2.Wafv2Client;
import software.amazon.awssdk.services.wafv2.model.*;
import java.util.ArrayList;

public class AWSWAFUtils {

    public AWSWAFUtils() {

    }

    public String createIPSetRequest(String ipsetName, ArrayList<String> ipAddresses) {
        Region region = Region.US_EAST_1;
        Wafv2Client client = Wafv2Client.builder().region(region).build();

        CreateIpSetRequest request = CreateIpSetRequest.builder()
                .name(ipsetName)
                .scope(Scope.REGIONAL)
                .ipAddressVersion(IPAddressVersion.IPV4)
                .addresses(ipAddresses)
                .build();

        CreateIpSetResponse response = client.createIPSet(request);
        return response.summary().id();
    }

    public void updateIPSetRequest(String id, String ipsetName, ArrayList<String> ipAddresses) {
        Region region = Region.US_EAST_1;
        Wafv2Client client = Wafv2Client.builder().region(region).build();

        UpdateIpSetRequest request = UpdateIpSetRequest.builder()
                .id(id)
                .scope(Scope.REGIONAL)
                .name(ipsetName)
                .addresses(ipAddresses)
                .build();

        client.updateIPSet(request);
    }

    public void deleteIPSetRequest(String id) {
        Region region = Region.US_EAST_1;
        Wafv2Client client = Wafv2Client.builder().region(region).build();

        DeleteIpSetRequest request = DeleteIpSetRequest.builder().id(id).build();

        client.deleteIPSet(request);
    }
}


