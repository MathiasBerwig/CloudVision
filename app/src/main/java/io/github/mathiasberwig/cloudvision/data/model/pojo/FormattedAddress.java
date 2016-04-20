package io.github.mathiasberwig.cloudvision.data.model.pojo;

import java.util.ArrayList;

/**
 * POJO class containing partial response mapping of a Google Maps Geocoding Web API response.
 *
 * Created as simple as possible by mathias.berwig on 14/04/2016.
 */
public class FormattedAddress {

    ArrayList<Result> results;

    public class Result {
        String formatted_address;
    }

    public String getFormattedAddress() {
        return results == null || results.size() == 0 ? null : results.get(0).formatted_address;
    }
}
