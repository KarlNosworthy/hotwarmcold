package com.karlnosworthy.beacons;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

public class HotWarmColdActivity extends Activity {

    private static final String TAG = HotWarmColdActivity.class.getSimpleName();

	private BeaconManager beaconManager;
	private Region region;

	
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    final TextView rangeLabel = (TextView) findViewById(R.id.range_label);
        final TextView beaconMacAddressLabel = (TextView) findViewById(R.id.beacon_mac_address_label);
	    final TextView beaconUuidLabel = (TextView) findViewById(R.id.beacon_uuid_label);
	    final TextView beaconVersionLabel = (TextView) findViewById(R.id.beacon_major_minor_label);
	    final TextView beaconStatsLabel = (TextView) findViewById(R.id.beacon_stats_label);
	    
		rangeLabel.setTextColor(Color.BLACK);
	    rangeLabel.setText(R.string.question_mark);
	    region = new Region("regionid", null, null, null);

	    beaconManager = new BeaconManager(this);
	    beaconManager.setRangingListener(new RangingListener() {
			@Override
			public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
				runOnUiThread(new Runnable() {
					
			          @Override
			          public void run() {
			        	  if (!beacons.isEmpty()) {

			        		  Beacon closestBeacon = beacons.get(0);

                              beaconMacAddressLabel.setText(getString(R.string.mac_address) + " : " + closestBeacon.getMacAddress());
                              beaconUuidLabel.setText(closestBeacon.getProximityUUID());
                              beaconVersionLabel.setText(getString(R.string.major) + ": "+closestBeacon.getMajor() +
                                                         "   " + getString(R.string.minor) + ": " + closestBeacon.getMinor());

			        		  beaconStatsLabel.setText(getString(R.string.power) + ": " +
			        				  		  		closestBeacon.getMeasuredPower() +
			        				  		  		" " +
			        				  		  		getString(R.string.dbm) +
			        				  		  		" |  " +
			        				  		  		getString(R.string.rssi) + 
			        				  		  		": " + closestBeacon.getRssi());

                              double accuracy = Utils.computeAccuracy(closestBeacon);

			        		  switch (Utils.proximityFromAccuracy(accuracy)) {
			        		  	case FAR:
			        		  		rangeLabel.setText(R.string.cold);
						        	rangeLabel.setTextColor(getResources().getColor(R.color.cold_colour));
						        	rangeLabel.setBackgroundResource(R.drawable.cold_indicator_background);
			        		  		break;
			        		  	case NEAR:
			        		  		rangeLabel.setText(R.string.warm);
			        		  		rangeLabel.setTextColor(getResources().getColor(R.color.warm_colour));
						        	rangeLabel.setBackgroundResource(R.drawable.warm_indicator_background);
			        		  		break;
			        		  	case IMMEDIATE:
			        		  		rangeLabel.setText(R.string.hot);
			        		  		rangeLabel.setTextColor(getResources().getColor(R.color.hot_colour));
						        	rangeLabel.setBackgroundResource(R.drawable.hot_indicator_background);
			        		  		break;
			        		  	case UNKNOWN:
						        	rangeLabel.setText(R.string.question_mark);
						        	rangeLabel.setTextColor(Color.BLACK);
						        	rangeLabel.setBackgroundResource(R.drawable.indicator_background);
			        		  		break;
			        		  }
			        	  }
			          }
				});
			}
		});
    }
	 
	@Override
	protected void onStart() {
		super.onStart();

	    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
	    	
	      @Override
	      public void onServiceReady() {
	        try {
	          beaconManager.startRanging(region);
	        } catch (RemoteException e) {
	          Toast.makeText(HotWarmColdActivity.this, getString(R.string.cannot_start_ranging_something_wrong),
	              Toast.LENGTH_LONG).show();
	          Log.e("", getString(R.string.cannot_start_ranging), e);
	        }
	      }
	    });
	}

	@Override
	protected void onStop() {
		beaconManager.disconnect();
		super.onStop();
	}	 
}
