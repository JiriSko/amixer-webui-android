package cz.jiriskorpil.amixerwebui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.control.Control;
import cz.jiriskorpil.amixerwebui.control.ControlContainer;
import cz.jiriskorpil.amixerwebui.control.ControlContainerAdapter;
import cz.jiriskorpil.amixerwebui.control.ControlContainerType;
import cz.jiriskorpil.amixerwebui.control.ControlFactory;
import cz.jiriskorpil.amixerwebui.control.mixer.MixerControl;
import cz.jiriskorpil.amixerwebui.task.AsyncHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ChangeCardHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveCardHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveCardsHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveControlsHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveHostnameHttpRequestTask;

/**
 * Class which supplies (main) activity with data.
 */
public class DataHandler
{
	private Context context;
	private OnFailListener mOnFailListener;

	/* GUI components */
	private RecyclerView listView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private List<ControlContainer> controls;

	private boolean downloadEnabled = true;
	private String lastUrl = "";
	private String cardId = "";

	/**
	 * @param context            The context to use. Usually {@link android.app.Activity} object.
	 * @param resultListView     ListView for results
	 * @param swipeRefreshLayout layout for results
	 */
	public DataHandler(Context context, RecyclerView resultListView, SwipeRefreshLayout swipeRefreshLayout)
	{
		this.context = context;

		this.listView = resultListView;
		this.listView.setHasFixedSize(true);
		this.listView.setLayoutManager(new GridLayoutManager(context, context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1, GridLayoutManager.VERTICAL, false));

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
		if (!downloadEnabled) return this;
		downloadEnabled = false;

		swipeRefreshLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				swipeRefreshLayout.setRefreshing(true);
				new RetrieveControlsHttpRequestTask(context, getBaseUrl(), new AsyncHttpRequestTask.OnFinishListener()
				{
					@Override
					public void onFinish(String result)
					{
						try {
							downloadEnabled = true;
							lastUrl = getBaseUrl();
							displayData(result.equals("") ? null : new JSONArray(result));
						} catch (JSONException e) {
							Log.e("JSONException", "Error: " + e.toString());
						}
					}
				}).execute();

				if (!lastUrl.equals(getBaseUrl())) {
					cardId = "";
					setupSoundCards();
				}
			}
		});
		return this;
	}

	private void setupSoundCards()
	{
		final MainActivity activity = (MainActivity) context;
		activity.getSupportActionBar().setSubtitle("");

		new RetrieveHostnameHttpRequestTask(context, getBaseUrl(), new AsyncHttpRequestTask.OnFinishListener() {
			@Override
			public void onFinish(String result) {
				activity.getSupportActionBar().setSubtitle(result);
			}
		}).execute();

		final MenuItem soundCard = activity.menu.findItem(R.id.action_card);
		soundCard.setVisible(false);
		new RetrieveCardsHttpRequestTask(context, getBaseUrl(), new AsyncHttpRequestTask.OnFinishListener() {
			@Override
			public void onFinish(String result) {
				try {
					JSONObject cards = (JSONObject) (new JSONArray("[" + result + "]")).get(0);

					if (cards.length() > 1) {
						soundCard.setVisible(true);

						final List<String> cardIds = new ArrayList<>();
						List<String> cardNames = new ArrayList<>();

						Iterator<String> iter = cards.keys();
						while (iter.hasNext()) {
							String id = iter.next();
							cardNames.add((String) cards.get(id));
							cardIds.add(id);
						}

						final Spinner spinner = (Spinner) MenuItemCompat.getActionView(soundCard);
						ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, cardNames);
						adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						spinner.setAdapter(adapter);

						spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
								if (cardId.equals(cardIds.get(i)) || cardId.equals("")) {
									return;
								}

								(new ChangeCardHttpRequestTask(context, getBaseUrl(), new AsyncHttpRequestTask.OnFinishListener() {
									@Override
									public void onFinish(String result) {
										download();
									}
								})).execute(cardIds.get(i));
								cardId = cardIds.get(i);
							}

							@Override
							public void onNothingSelected(AdapterView<?> adapterView) {
							}
						});

						new RetrieveCardHttpRequestTask(context, getBaseUrl(), new AsyncHttpRequestTask.OnFinishListener() {
							@Override
							public void onFinish(String result) {
								cardId = result;
								spinner.setSelection(cardIds.indexOf(result), false);
							}
						}).execute();
					}
				} catch (JSONException e) {
					Log.e("JSONException", "Error: " + e.toString());
				}
			}
		}).execute();
	}

	public DataHandler setOnFailListener(OnFailListener listener)
	{
		mOnFailListener = listener;
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
			if (mOnFailListener != null) {
				mOnFailListener.onFail();
				lastUrl = "";
				cardId = "";
			}
		} else {
			controls = parseControls(jsonArray);
		}

		listView.setAdapter(new ControlContainerAdapter(context, controls));
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
				}/* else {
					// not supported (yet?)
				}*/
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
	 * Returns base url to server (http://<ipAddress>:<port>)
	 *
	 * @return server url
	 */
	public String getBaseUrl()
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String ipAddress = preferences.getString("ip_address", context.getResources().getString(R.string.pref_default_ip_address));
		String port = preferences.getString("port", context.getResources().getString(R.string.pref_default_port));
		return "http://" + ipAddress + ":" + port;
	}


	public interface OnFailListener
	{
		/**
		 * Called when downloading failed.
		 */
		void onFail();
	}
}
