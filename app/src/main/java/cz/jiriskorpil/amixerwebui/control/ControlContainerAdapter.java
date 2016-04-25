package cz.jiriskorpil.amixerwebui.control;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.activity.MainActivity;
import cz.jiriskorpil.amixerwebui.task.ChangeSourceHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ChangeVolumeHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ToggleControlHttpRequestTask;

/**
 * List adapter for control container.
 */
public class ControlContainerAdapter extends ArrayAdapter<ControlContainer>
{
	Context context;
	int layoutResourceId;
	List<ControlContainer> data = null;

	private static final float FULL_VISIBILITY = 1;
	private static final float LOW_VISIBILITY = (float) 0.4;

	public ControlContainerAdapter(Context context, int layoutResourceId, List<ControlContainer> data)
	{
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		ControlContainerHolder holder;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ControlContainerHolder();
			holder.enabled = (Switch) row.findViewById(R.id.enabled);
			holder.name = (TextView) row.findViewById(R.id.name);
			holder.bind_sliders = (CheckBox) row.findViewById(R.id.bind_sliders);
			holder.source_list = (RadioGroup) row.findViewById(R.id.source_list);
			holder.channels_list = (LinearLayout) row.findViewById(R.id.channels_list);

			row.setTag(holder);

		} else {
			holder = (ControlContainerHolder) row.getTag();
		}

		setupCard(data.get(position), holder, row);

		return row;
	}

	/**
	 * Setups control card.
	 *
	 * @param container
	 * @param holder
	 * @param row
	 */
	private void setupCard(ControlContainer container, ControlContainerHolder holder, View row)
	{
		setupCardHeader(container, holder);

		setupCardBodySource(container, holder);
		setupCardBodyChannels(container, holder, row);

		setCardBodyAlpha(container, holder);
	}

	/**
	 * Sets card body alpha color part based on boolean control value.
	 *
	 * @param container
	 * @param holder
	 */
	private void setCardBodyAlpha(ControlContainer container, ControlContainerHolder holder)
	{
		if (container.hasSwitchControl()) {
			if (container.getSwitch().isChecked())
			{
				holder.source_list.setAlpha(FULL_VISIBILITY);
				holder.channels_list.setAlpha(FULL_VISIBILITY);
			} else
			{
				holder.source_list.setAlpha(LOW_VISIBILITY);
				holder.channels_list.setAlpha(LOW_VISIBILITY);
			}
		}
	}

	/**
	 * Changes boolean control value including initialize of request to server.
	 *
	 * @param container
	 * @param holder
	 * @param isChecked
	 */
	private void toggleControl(final ControlContainer container, final ControlContainerHolder holder, boolean isChecked)
	{
		container.getSwitch().setChecked(isChecked);
		setCardBodyAlpha(container, holder);
		new ToggleControlHttpRequestTask((Activity) getContext(), ((MainActivity) getContext()).getDataHandler().getBaseUrl())
				.execute(String.valueOf(container.getSwitch().getId()), container.getSwitch().isChecked() ? "1" : "0");
	}

	/**
	 * Setups card header (Switch, TextView and CheckBox values, settings and listeners).
	 *
	 * @param container
	 * @param holder
	 */
	private void setupCardHeader(final ControlContainer container, final ControlContainerHolder holder)
	{
		if (container.hasSwitchControl()) // set check value and add onClick listener
		{
			holder.enabled.setVisibility(View.VISIBLE);
			holder.enabled.setChecked(container.getSwitch().isChecked());
			holder.enabled.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					toggleControl(container, holder, holder.enabled.isChecked());
				}
			});
			holder.name.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					toggleControl(container, holder, !holder.enabled.isChecked());
					holder.enabled.setChecked(!holder.enabled.isChecked());
				}
			});
		} else { // or hide the switch
			holder.enabled.setVisibility(View.INVISIBLE);
		}

		holder.name.setText(container.getName());

		if (container.hasVolumeControl()) // if there is volume control then display bind checkbox and add listener
		{
			holder.bind_sliders.setVisibility(View.VISIBLE);
			holder.bind_sliders.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					Toast.makeText(getContext(),
							getContext().getResources().getString(holder.bind_sliders.isChecked() ? R.string.bound_sliders : R.string.unbounded_sliders),
							Toast.LENGTH_LONG).show();
					return true;
				}
			});
		} else {
			holder.bind_sliders.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Setups part of cards body - source radio buttons (values, settings and listeners).
	 *
	 * @param container
	 * @param holder
	 */
	private void setupCardBodySource(final ControlContainer container, final ControlContainerHolder holder)
	{
		holder.source_list.removeAllViews();
		if (container.hasSourceControl())
		{
			holder.source_list.setVisibility(View.VISIBLE);
			holder.source_list.check(0);
			for (int i = 0; i < container.getSource().getItems().length; i++)
			{
				final RadioButton button = new RadioButton(getContext());
				button.setId(i);
				button.setText(container.getSource().getItems()[i]);
				button.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						container.getSource().setValue(holder.source_list.getCheckedRadioButtonId());
						new ChangeSourceHttpRequestTask((Activity) getContext(), ((MainActivity) getContext()).getDataHandler().getBaseUrl())
								.execute(String.valueOf(container.getSource().getId()), String.valueOf(button.getId()));
					}
				});
				holder.source_list.addView(button);
			}
			holder.source_list.check(container.getSource().getValue());
		} else {
			holder.source_list.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Setups part of cards body - channels (values, settings nad listeners).
	 *
	 * @param container
	 * @param holder
	 * @param row
	 */
	private void setupCardBodyChannels(final ControlContainer container, final ControlContainerHolder holder, View row)
	{
		holder.channels_list.removeAllViews();
		if (container.hasVolumeControl())
		{
			for (int i = 0; i < container.getVolume().getChannels().length; i++)
			{
				LinearLayout channelLayout = (LinearLayout) row.findViewById(R.id.control_channel);
				View channelView = ((Activity) context).getLayoutInflater().inflate(R.layout.control_channel, channelLayout, false);
				holder.channels_list.addView(channelView, i);

				TextView channel_name = (TextView) channelView.findViewById(R.id.channel_name);
				SeekBar channel_volume_seek_bar = (SeekBar) channelView.findViewById(R.id.channel_volume_seek_bar);
				final TextView channel_volume = (TextView) channelView.findViewById(R.id.channel_volume);

				final IChannel channel = container.getVolume().getChannels()[i];

				channel_name.setText(channel.getName());
				channel_volume_seek_bar.setMax(channel.getControl().getMax());
				channel_volume_seek_bar.setProgress(channel.getValue());
				channel_volume.setText(Math.round(100 * (double) channel.getValue() / channel.getControl().getMax()) + " %");

				channel_volume_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
				{
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
					{
						String progressMsg = Math.round(100 * progress / seekBar.getMax()) + " %";
						if (fromUser && holder.bind_sliders.isChecked()) {
							for (int i = 0; i < container.getVolume().getChannels().length; i++) {
								if (!holder.channels_list.getChildAt(i).equals(seekBar))
								{
									container.getVolume().getChannels()[i].setValue(progress);
									View channelView = holder.channels_list.getChildAt(i);
									((SeekBar) channelView.findViewById(R.id.channel_volume_seek_bar))
											.setProgress(progress);
									((TextView) channelView.findViewById(R.id.channel_volume))
											.setText(progressMsg);
								}
							}
						}
						if (fromUser)
						{
							channel.setValue(progress);
							channel_volume.setText(progressMsg);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
						String volumes = "";
						for (int i = 0; i < container.getVolume().getChannels().length; i++)
						{
							if (!volumes.equals("")) {
								volumes += "/";
							}
							volumes += String.valueOf(container.getVolume().getChannels()[i].getValue());
						}
						new ChangeVolumeHttpRequestTask((Activity) getContext(), ((MainActivity) getContext()).getDataHandler().getBaseUrl())
								.execute(String.valueOf(container.getVolume().getId()), volumes);
					}
				});
			}
		}
	}

	/**
	 * Holds ControlContainer GUI controls.
	 */
	static class ControlContainerHolder
	{
		Switch enabled;
		TextView name;
		CheckBox bind_sliders;
		RadioGroup source_list;
		LinearLayout channels_list;
	}
}
