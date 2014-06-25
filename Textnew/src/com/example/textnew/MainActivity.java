package com.example.textnew;

/*
 *ibeacon uuid: d26d197e-4a1c-44ae-b504-dd7768870564
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconData;
import com.radiusnetworks.ibeacon.IBeaconDataNotifier;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.radiusnetworks.ibeacon.client.DataProviderException;

import android.R.attr;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity implements IBeaconConsumer
{

	protected static final String TAG = "MainActivity";
	private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		verifyBluetooth();
		iBeaconManager.bind(this);
	}

	public void onRangingClicked(View view)
	{

	}

	public void onBackgroundClicked(View view)
	{

	}

	@SuppressLint("NewApi")
	private void verifyBluetooth()
	{
		IBeaconManager.getInstanceForApplication(this).checkAvailability();

		try
		{
			if (!IBeaconManager.getInstanceForApplication(this).checkAvailability())
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						// finish();
						// System.exit(0);
					}
				});
				builder.show();
			}
		} catch (RuntimeException e)
		{
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener()
			{

				@Override
				public void onDismiss(DialogInterface dialog)
				{
					// finish();
					// System.exit(0);
				}

			});
			builder.show();

		}

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		iBeaconManager.unBind(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (iBeaconManager.isBound(this))
			iBeaconManager.setBackgroundMode(this, true);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (iBeaconManager.isBound(this))
			iBeaconManager.setBackgroundMode(this, false);
	}

	private void logToDisplay(final String line)
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				TextView textView = (TextView) MainActivity.this.findViewById(R.id.textView1);
				textView.append(line + "\n\n");
			}
		});
	}

	@Override
	public void onIBeaconServiceConnect()
	{
		iBeaconManager.setMonitorNotifier(new MonitorNotifier()
		{
			@Override
			public void didEnterRegion(Region region)
			{
				logToDisplay("I just saw an iBeacon UUID " + region.getProximityUuid() + " for the first time!");
			}

			@Override
			public void didExitRegion(Region region)
			{
				logToDisplay("I no longer see an iBeacon named " + region.getProximityUuid());
			}

			@Override
			public void didDetermineStateForRegion(int state, Region region)
			{
				logToDisplay("I have switched from seeing/not seeing iBeacons: " + state);
			}
		});
		iBeaconManager.setRangeNotifier(new RangeNotifier()
		{

			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> arg0, Region arg1)
			{
				// TODO Auto-generated method stub
				Iterator<IBeacon> iterator = arg0.iterator();
				while (iterator.hasNext())
				{
					IBeacon iBeacon = (IBeacon) iterator.next();
					logToDisplay("ibeacon major:" + iBeacon.getMajor());

				}

			}
		});

		try
		{
			iBeaconManager.startRangingBeaconsInRegion(new Region("myMonitoringUniqueId",
					"d26d197e-4a1c-44ae-b504-dd7768870564", null, null));
			iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId",
					"d26d197e-4a1c-44ae-b504-dd7768870564", null, null));
		} catch (RemoteException e)
		{
		}
	}

}
