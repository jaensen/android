package apollong.dspace;

import android.location.Location;
import android.webkit.JavascriptInterface;

public class JsApi {
    LocationService _locationService = null;

    JsApi(LocationService locationService) {
        _locationService = locationService;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public JsLocationDto getLastLocation() {
        Location loc = _locationService.getLastLocation();

        JsLocationDto dto = new JsLocationDto();
        dto.lat = loc.getLatitude();
        dto.lon = loc.getLongitude();
        dto.time = loc.getTime();

        return dto;
    }
}