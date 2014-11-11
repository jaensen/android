package apollong.dspace;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayDeque;

public class LocationService extends Service implements LocationListener {

    private final Context _context;
    boolean _isRunning = false;

    static final int LOC_BUFFER_SIZE = 100;
    static final int TRIGGER_DISTANCE = 10;
    static final int TRIGGER_TIME = 1000 * 60 * 1;

    LocationManager _locationManager;
    ArrayDeque<Location> _locationBuffer = new ArrayDeque<Location>(LOC_BUFFER_SIZE);

    public LocationService(Context context) {
        _context = context;
    }

    IBinder _locationServiceBinder = new LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    public Location getLastLocation() {
        return _locationBuffer.peekLast();
    }

    public void start()
            throws GpsNotReadyException
    {
        _locationManager = (LocationManager) _context.getSystemService(LOCATION_SERVICE);

        if (!_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            throw new GpsNotReadyException();

        _locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                TRIGGER_TIME,
                TRIGGER_DISTANCE, this);

        _isRunning = true;
    }

    public void stop() {
        _locationManager.removeUpdates(LocationService.this);
        _isRunning = false;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return _locationServiceBinder;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (_locationBuffer.size() == LOC_BUFFER_SIZE)
            _locationBuffer.remove();

        _locationBuffer.addLast(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}