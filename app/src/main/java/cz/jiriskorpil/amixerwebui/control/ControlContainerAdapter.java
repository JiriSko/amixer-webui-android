package cz.jiriskorpil.amixerwebui.control;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.activity.DataHandler;
import cz.jiriskorpil.amixerwebui.task.ChangeSourceHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ChangeVolumeHttpRequestTask;
import cz.jiriskorpil.amixerwebui.task.ToggleControlHttpRequestTask;

/**
 * List adapter for control container.
 */
public class ControlContainerAdapter extends RecyclerView.Adapter<ControlContainerAdapter.ControlContainerHolder>
{
	private Context context;
	private List<ControlContainer> data = null;

	private static final float FULL_VISIBILITY = 1;
	private static final float LOW_VISIBILITY = (float) 0.4;

	/**
	 * @param context          The context to use. Usually {@link android.app.Activity} object.
	 * @param data             list of controls
	 */
	public ControlContainerAdapter(Context context, List<ControlContainer> data)
	{
		this.context = context;
		this.data = data;
	}

	@Override
	public int getItemCount()
	{
		return data.size();
	}

	@Override
	public void onBindViewHolder(ControlContainerHolder holder, int i)
	{
		ControlContainer container = data.get(i);

		setupCard(container, holder);
	}

	@Override
	public ControlContainerHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{
		View itemView = LayoutInflater
							.from(viewGroup.getContext())
							.inflate(R.layout.control_row, viewGroup, false);

		return new ControlContainerHolder(itemView);
	}

	/**
	 * Setups control card.
	 *
	 * @param container control container
	 * @param holder    control container holder
	 */
	private void setupCard(ControlContainer container, ControlContainerHolder holder)
	{
		setupCardHeader(container, holder);

		setupCardBodySource(container, holder);
		setupCardBodyChannels(container, holder);

		setCardBodyAlpha(container, holder);
	}

	/**
	 * Sets card body alpha color part based on boolean control value.
	 *
	 * @param container control container
	 * @param holder    control container holder
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
	 * @param container control container
	 * @param holder    control container holder
	 * @param isChecked true if boolean control is checked else false
	 */
	private void toggleControl(final ControlContainer container, final ControlContainerHolder holder, boolean isChecked)
	{
		container.getSwitch().setChecked(isChecked);
		setCardBodyAlpha(container, holder);
		new ToggleControlHttpRequestTask(holder.getView().getContext(), DataHandler.getBaseUrl(holder.getView().getContext()))
				.execute(String.valueOf(container.getSwitch().getId()), container.getSwitch().isChecked() ? "1" : "0");
	}

	/**
	 * Setups card header (Switch, TextView and CheckBox values, settings and listeners).
	 *
	 * @param container control container
	 * @param holder    control container holder
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
					Toast.makeText(holder.getView().getContext(),
							holder.getView().getContext().getResources().getString(holder.bind_sliders.isChecked() ? R.string.sliders_locked : R.string.sliders_unlocked),
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
	 * @param container control container
	 * @param holder    control container holder
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
				final RadioButton button = new RadioButton(holder.getView().getContext());
				button.setId(i + 1);
				button.setText(container.getSource().getItems()[i]);
				button.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						container.getSource().setValue(holder.source_list.getCheckedRadioButtonId());
						new ChangeSourceHttpRequestTask(holder.getView().getContext(), DataHandler.getBaseUrl(holder.getView().getContext()))
								.execute(String.valueOf(container.getSource().getId()), String.valueOf(button.getId()));
					}
				});
				holder.source_list.addView(button);
			}
			holder.source_list.check(container.getSource().getValue() + 1);
		} else {
			holder.source_list.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Setups part of cards body - channels (values, settings nad listeners).
	 *
	 * @param container control container
	 * @param holder    control container holder
	 */
	private void setupCardBodyChannels(final ControlContainer container, final ControlContainerHolder holder)
	{
		holder.channels_list.removeAllViews();
		if (container.hasVolumeControl())
		{
			for (int i = 0; i < container.getVolume().getChannels().length; i++)
			{
				LinearLayout channelLayout = (LinearLayout) holder.getView().findViewById(R.id.control_channel);
				View channelView = ((Activity) context).getLayoutInflater().inflate(R.layout.control_channel, channelLayout, false);
				holder.channels_list.addView(channelView, i);

				TextView channelName = (TextView) channelView.findViewById(R.id.channel_name);
				SeekBar channelVolumeSeekBar = (SeekBar) channelView.findViewById(R.id.channel_volume_seek_bar);
				final TextView channelVolume = (TextView) channelView.findViewById(R.id.channel_volume);

				final IChannel channel = container.getVolume().getChannels()[i];

				channelName.setText(channel.getName());
				channelVolumeSeekBar.setMax(channel.getControl().getMax());
				channelVolumeSeekBar.setProgress(channel.getValue());
				channelVolume.setText(holder.getView().getContext().getString(R.string.channel_value, Math.round(100 * (double) channel.getValue() / channel.getControl().getMax())));

				channelVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
				{
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
					{
						String progressMsg = holder.getView().getContext().getString(R.string.channel_value, Math.round(100 * progress / seekBar.getMax()));
						if (fromUser && holder.bind_sliders.isChecked()) {
							for (int j = 0; j < container.getVolume().getChannels().length; j++) {
								if (!holder.channels_list.getChildAt(j).equals(seekBar))
								{
									container.getVolume().getChannels()[j].setValue(progress);
									View channelView = holder.channels_list.getChildAt(j);
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
							channelVolume.setText(progressMsg);
						}
					}

					@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
						String volumes = "";
						for (int j = 0; j < container.getVolume().getChannels().length; j++)
						{
							if (!"".equals(volumes)) {
								volumes += "/";
							}
							volumes += String.valueOf(container.getVolume().getChannels()[j].getValue());
						}
						new ChangeVolumeHttpRequestTask(holder.getView().getContext(), DataHandler.getBaseUrl(holder.getView().getContext()))
								.execute(String.valueOf(container.getVolume().getId()), volumes);
					}
				});
			}
		}
	}

	/**
	 * Holds ControlContainer GUI controls.
	 */
	static class ControlContainerHolder extends RecyclerView.ViewHolder
	{
		View v;

		SwitchCompat enabled;
		TextView name;
		CheckBox bind_sliders;
		RadioGroup source_list;
		LinearLayout channels_list;

		ControlContainerHolder(View v)
		{
			super(v);
			this.v = v;
			enabled = (SwitchCompat) v.findViewById(R.id.enabled);
			name = (TextView) v.findViewById(R.id.name);
			bind_sliders = (CheckBox) v.findViewById(R.id.bind_sliders);
			source_list = (RadioGroup) v.findViewById(R.id.source_list);
			channels_list = (LinearLayout) v.findViewById(R.id.channels_list);
		}


		public View getView()
		{
			return v;
		}
	}
}
