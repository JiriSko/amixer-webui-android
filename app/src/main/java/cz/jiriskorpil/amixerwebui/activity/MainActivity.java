package cz.jiriskorpil.amixerwebui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cz.jiriskorpil.amixerwebui.R;

/**
 * Main application activity.
 */
public class MainActivity extends AppCompatActivity
{
	public static final int EQUALIZER_REQUEST = 1;

	private DataHandler dataHandler;
	private String lastUrl;
	protected Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		dataHandler = (new DataHandler(this,
					(RecyclerView) findViewById(R.id.controls_list),
					(SwipeRefreshLayout) findViewById(R.id.swipe_container)))
				.setOnFailListener(new DataHandler.OnFailListener()
				{
					@Override
					public void onFail()
					{
						Snackbar
								.make(findViewById(android.R.id.content),
										getResources().getString(R.string.msg_connection_error),
										Snackbar.LENGTH_LONG)
								.setAction(getResources().getString(R.string.retry_btn_title), new View.OnClickListener()
								{
									@Override
									public void onClick(View v)
									{
										dataHandler.download();
									}
								}).setActionTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLight))
								.show();
					}
				});
		lastUrl = DataHandler.getBaseUrl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		GridLayoutManager manager = (GridLayoutManager) ((RecyclerView) findViewById(R.id.controls_list)).getLayoutManager();
		manager.setSpanCount(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		if (!lastUrl.equals(DataHandler.getBaseUrl(this)))
		{
			dataHandler.download();
			lastUrl = DataHandler.getBaseUrl(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		this.menu = menu;
		getMenuInflater().inflate(R.menu.menu_main, menu);

		dataHandler.download();

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		switch (id)
		{
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;

			case R.id.action_equalizer:
				Intent intent = new Intent(this, EqualizerActivity.class);
				intent.putExtra("equalizer", dataHandler.getEqualizer());
				startActivityForResult(intent, EQUALIZER_REQUEST);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case EQUALIZER_REQUEST:
				dataHandler.setupEqualizer();
				break;

			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
