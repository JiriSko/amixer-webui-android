package cz.jiriskorpil.amixerwebui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
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
import cz.jiriskorpil.amixerwebui.control.ControlContainer;
import cz.jiriskorpil.amixerwebui.control.ControlContainerAdapter;
import cz.jiriskorpil.amixerwebui.control.ControlParser;
import cz.jiriskorpil.amixerwebui.control.Equalizer;
import cz.jiriskorpil.amixerwebui.task.AsyncHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ChangeCardHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveCardHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveCardsHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveControlsHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.RetrieveEqualizerHttpRequestTask;
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

	private boolean downloadEnabled = true;
	private String lastUrl = "";
	private String cardId = "";

	private Equalizer equalizer;

	/**
	 * @param context            The context to use. Usually {@link android.app.Activity} object.
	 * @param resultListView     ListView for results
	 * @param swipeRefreshLayout layout for results
	 */
	DataHandler(Context context, RecyclerView resultListView, SwipeRefreshLayout swipeRefreshLayout)
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
	DataHandler download()
	{
		if (!downloadEnabled) {
			return this;
		}
		downloadEnabled = false;

		swipeRefreshLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				swipeRefreshLayout.setRefreshing(true);
				new RetrieveControlsHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener()
				{
					@Override
					public void onFinish(String result)
					{
						try
						{
							downloadEnabled = true;
							lastUrl = getBaseUrl(context);
							displayData("".equals(result) ? null : new JSONArray(result));
						} catch (JSONException e) {
							Log.e("JSONException", "Error: " + e.toString());
						}
					}
				}).execute();

				if (!lastUrl.equals(getBaseUrl(context)))
				{
					cardId = "";
					setupHostname();
					setupSoundCards();
				}
				setupEqualizer();
			}
		});
		return this;
	}

	private void setupHostname()
	{
		final MainActivity activity = (MainActivity) context;
		if (activity.getSupportActionBar() != null) {
			activity.getSupportActionBar().setSubtitle("");
		}

		new RetrieveHostnameHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener() {
			@Override
			public void onFinish(String result) {
				activity.getSupportActionBar().setSubtitle(result);
			}
		}).execute();
	}

	private void setupSoundCards()
	{
		final MenuItem soundCard = ((MainActivity) context).menu.findItem(R.id.action_card);
		soundCard.setVisible(false);
		new RetrieveCardsHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener() {
			@Override
			public void onFinish(String result) {
				try
				{
					JSONObject cards = "[]".equals(result) ?  new JSONObject() : (JSONObject) (new JSONArray("[" + result + "]")).get(0);

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

						final Spinner spinner = (Spinner) soundCard.getActionView();
						ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, cardNames);
						adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						spinner.setAdapter(adapter);

						spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
								if (cardId.equals(cardIds.get(i)) || "".equals(cardId)) {
									return;
								}

								(new ChangeCardHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener() {
									@Override
									public void onFinish(String result) {
										download();
									}
								})).execute(cardIds.get(i));
								cardId = cardIds.get(i);
							}

							@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
							@Override
							public void onNothingSelected(AdapterView<?> adapterView) {
							}
						});

						new RetrieveCardHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener() {
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

	void setupEqualizer()
	{
		final MainActivity activity = (MainActivity) context;

		new RetrieveEqualizerHttpRequestTask(context, getBaseUrl(context), new AsyncHttpRequestTask.OnFinishListener() {
			@Override
			public void onFinish(String result) {
				try
				{
					JSONArray data = new JSONArray(result);
					if (data.length() > 0) {
						activity.menu.findItem(R.id.action_equalizer).setVisible(true);
						equalizer = new Equalizer(result);
					}
				} catch (JSONException e) {
					Log.e("JSONException", "Error: " + e.toString());
				}
			}
		}).execute();
	}

	DataHandler setOnFailListener(OnFailListener listener)
	{
		mOnFailListener = listener;
		return this;
	}

	/**
	 * Displays downloaded data.
	 *
	 * @param jsonArray JSON array representing controls retrieved from server
	 */
	private void displayData(JSONArray jsonArray)
	{
		List<ControlContainer> controls = new ArrayList<>();

		if (jsonArray == null) {
			if (mOnFailListener != null) {
				mOnFailListener.onFail();
				lastUrl = "";
				cardId = "";
			}
		} else {
			controls = ControlParser.parse(jsonArray);
		}

		listView.setAdapter(new ControlContainerAdapter(context, controls));
		swipeRefreshLayout.setRefreshing(false);
	}

	/**
	 * Returns base url to server (http://<ipAddress>:<port>)
	 *
	 * @return server url
	 */
	public static String getBaseUrl(Context context)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String ipAddress = preferences.getString("ip_address", context.getResources().getString(R.string.pref_default_ip_address));
		String port = preferences.getString("port", context.getResources().getString(R.string.pref_default_port));
		return "http://" + ipAddress + ":" + port;
	}

	public Equalizer getEqualizer() {
		return equalizer;
	}

	interface OnFailListener
	{
		/**
		 * Called when downloading failed.
		 */
		void onFail();
	}
}
