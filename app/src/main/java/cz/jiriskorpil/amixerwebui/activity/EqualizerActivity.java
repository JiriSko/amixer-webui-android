package cz.jiriskorpil.amixerwebui.activity;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.control.Equalizer;
import cz.jiriskorpil.amixerwebui.control.EqualizerAdapter;

/**
 * Equalizer activity.
 */
public class EqualizerActivity extends AppCompatActivity {

	private LinearLayoutManager manager;

	private boolean lockedSliders;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equalizer);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		Equalizer equalizer = getIntent().getParcelableExtra("equalizer");

		manager = new LinearLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false);

		RecyclerView equalizerList = (RecyclerView) findViewById(R.id.equalizer_list);
		equalizerList.setLayoutManager(manager);
		equalizerList.setAdapter(new EqualizerAdapter(this, equalizer.getControls()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		manager.setOrientation(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_equalizer, menu);

		setupCheckbox(menu);

		return true;
	}

	/**
	 * Setups checkbox in menu.
	 * @param menu menu
	 */
	private void setupCheckbox(Menu menu)
	{
		AppCompatCheckBox checkBox = (AppCompatCheckBox) menu.findItem(R.id.equalizer_lock).getActionView();
		ColorStateList colorStateList = new ColorStateList(new int[][]{
				new int[]{-android.R.attr.state_enabled}, // disabled
				new int[]{android.R.attr.state_enabled}, // enabled
		},
				new int[]{
						Color.WHITE, // disabled
						Color.WHITE // enabled
				});
		checkBox.setSupportButtonTintList(colorStateList);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b)
			{
				lockedSliders = b;
			}
		});
		checkBox.setText(getString(R.string.lock_sliders));
		checkBox.setChecked(true);
		checkBox.setPadding(0,0,Math.round(getResources().getDimension(R.dimen.activity_horizontal_margin)),0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public boolean isLockedSliders()
	{
		return lockedSliders;
	}
}
