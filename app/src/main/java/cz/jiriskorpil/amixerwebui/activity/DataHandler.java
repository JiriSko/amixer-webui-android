package cz.jiriskorpil.amixerwebui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.control.Control;
import cz.jiriskorpil.amixerwebui.control.ControlContainer;
import cz.jiriskorpil.amixerwebui.control.ControlContainerAdapter;
import cz.jiriskorpil.amixerwebui.control.ControlContainerType;
import cz.jiriskorpil.amixerwebui.control.ControlFactory;
import cz.jiriskorpil.amixerwebui.control.mixer.MixerControl;
import cz.jiriskorpil.amixerwebui.task.AsyncCallback;
import cz.jiriskorpil.amixerwebui.task.RetrieveControlsHttpRequestTask;

/**
 * Class which supplies (main) activity with data.
 */
public class DataHandler
{
	Activity activity;

	/* GUI components */
	ListView listView;
	SwipeRefreshLayout swipeRefreshLayout;
	List<ControlContainer> controls;

	/**
	 * @param activity           parent activity
	 * @param resultListView     ListView for results
	 * @param swipeRefreshLayout layout for results
	 */
	public DataHandler(Activity activity, ListView resultListView, SwipeRefreshLayout swipeRefreshLayout)
	{
		this.activity = activity;
		this.listView = resultListView;
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				download();
			}
		});
	}

	/**
	 * Creates request to download data
	 * @return self
	 */
	public DataHandler download()
	{
		swipeRefreshLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				swipeRefreshLayout.setRefreshing(true);
				new RetrieveControlsHttpRequestTask(activity, getBaseUrl(), new AsyncCallback()
				{
					@Override
					public void execute(String result)
					{
						try {
							displayData(result.equals("") ? null : new JSONArray(result));
						} catch (JSONException e) {
							Log.e("JSONException", "Error: " + e.toString());
						}
					}
				}).execute();
			}
		});
		return this;
	}

	/**
	 * Displays downloaded data.
	 *
	 * @param jsonArray JSON array representing controls retrieved from server
	 */
	public void displayData(JSONArray jsonArray)
	{
		List<ControlContainer> controls = new ArrayList<>();

		if (jsonArray == null) {
			showFailedSnackbar();
		} else {
			controls = parseControls(jsonArray);
		}

		ControlContainerAdapter adapter = new ControlContainerAdapter(activity, R.layout.control_row, controls);
		listView.setAdapter(adapter);
		swipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * Parses controls out of JSON array.
	 *
	 * @param jsonArray JSON array representing controls retrieved from server
	 * @return list of control containers
	 */
	private List<ControlContainer> parseControls(JSONArray jsonArray)
	{
		Control control;
		controls = new ArrayList<>();

		try {
			for (int i = 0; i < jsonArray.length(); i++)
			{
				control = ControlFactory.createControl(jsonArray.getJSONObject(i));
				if (control instanceof MixerControl) {
					getControlContainer(control.getName()).addControl(control);
				} else {
					// not supported (yet?)
				}
			}
		} catch (JSONException e) {
			Log.e("JSONException", "Error: " + e.toString());
		}
		return controls;
	}

	private ControlContainer getControlContainer(String controlName)
	{
		int index = findControlContainer(controlName);

		if (index == -1) // if doesn't exists then create new container
		{
			controls.add(new ControlContainer(controlName));
			index = controls.size() - 1;
		}

		return controls.get(index);
	}

	private int findControlContainer(String controlName)
	{
		for (int i = 0; i < controls.size(); i++) {
			if (controls.get(i).getName().equals(ControlContainerType.getContainerName(controlName))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Shows snackbar with info about download fail.
	 */
	private void showFailedSnackbar()
	{
		Snackbar
				.make(activity.findViewById(android.R.id.content),
						activity.getResources().getString(R.string.msg_connection_error),
						Snackbar.LENGTH_LONG)
				.setAction(activity.getResources().getString(R.string.retry_btn_title), new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						download();
					}
				}).setActionTextColor(ContextCompat.getColor(activity, R.color.colorPrimaryLight))
				.show();
	}

	/**
	 * Returns base url to server (http://<ipAddress>:<port>)
	 *
	 * @return server url
	 */
	public String getBaseUrl()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String ipAddress = preferences.getString("ip_address", activity.getResources().getString(R.string.pref_default_ip_address));
		String port = preferences.getString("port", activity.getResources().getString(R.string.pref_default_port));
		return "http://" + ipAddress + ":" + port;
	}
}
