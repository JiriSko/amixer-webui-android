package cz.jiriskorpil.amixerwebui.control;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.jiriskorpil.amixerwebui.R;
import cz.jiriskorpil.amixerwebui.activity.DataHandler;
import cz.jiriskorpil.amixerwebui.activity.EqualizerActivity;
import cz.jiriskorpil.amixerwebui.task.ChangeEqualizerHttpRequestTask;

/**
 * List adapter for equalizer control container.
 */
public class EqualizerAdapter extends RecyclerView.Adapter<EqualizerAdapter.ViewHolder>
{
	private Context context;
	private List<ControlContainer> data = null;

	/**
	 * @param context          The context to use. Usually {@link android.app.Activity} object.
	 * @param data             list of controls
	 */
	public EqualizerAdapter(Context context, List<ControlContainer> data)
	{
		this.context = context;
		this.data = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getItemCount()
	{
		return data.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBindViewHolder(EqualizerAdapter.ViewHolder holder, int position)
	{
		setupEqualizerRow(data.get(position), holder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.equalizer_row, parent, false);

		return new ViewHolder(itemView);
	}

	/**
	 * Setups equalizer row.
	 *
	 * @param controlContainer control container
	 * @param holder control container holder
	 */
	private void setupEqualizerRow(ControlContainer controlContainer, final ViewHolder holder)
	{
		if (!controlContainer.hasVolumeControl()) {
			return;
		}

		holder.channelsList.removeAllViews();

		final EqualizerActivity equalizerActivity = (EqualizerActivity) context;

		final IIntegerControl volume = controlContainer.getVolume();

		String name = controlContainer.getName();
		Pattern p = Pattern.compile("[0-9]+\\. ([0-9]+ k?Hz) .*");
		Matcher m = p.matcher(name);
		holder.channelName.setText(m.find() ? m.group(1) : name);

		for (final IChannel channel: volume.getChannels())
		{
			View channelView = ((Activity) context).getLayoutInflater().inflate(R.layout.equalizer_channel, holder.channelsList, false);

			SeekBar channelVolumeSeekBar = (SeekBar) channelView.findViewById(R.id.channel_volume_seek_bar);
			channelVolumeSeekBar.setMax(volume.getMax());
			channelVolumeSeekBar.setProgress(channel.getValue());
			channelVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
				{
					if (fromUser && equalizerActivity.isLockedSliders())
					{
						for (int i = 0; i < volume.getChannels().length; i++)
						{
							if (!holder.channelsList.getChildAt(i).equals(seekBar))
							{
								volume.getChannels()[i].setValue(progress);
								View channelView = holder.channelsList.getChildAt(i);
								((SeekBar) channelView.findViewById(R.id.channel_volume_seek_bar))
										.setProgress(progress);
							}
						}
					}
					if (fromUser)
					{
						channel.setValue(progress);
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
					for (int i = 0; i < volume.getChannels().length; i++)
					{
						if (!"".equals(volumes)) {
							volumes += "/";
						}
						volumes += String.valueOf(volume.getChannels()[i].getValue());
					}

					new ChangeEqualizerHttpRequestTask(holder.v.getContext(), DataHandler.getBaseUrl(holder.v.getContext()))
							.execute(String.valueOf(volume.getId()), volumes);
				}
			});

			holder.channelsList.addView(channelView);
		}
	}


	/**
	 * Holds GUI controls.
	 */
	static class ViewHolder extends RecyclerView.ViewHolder
	{
		View v;

		LinearLayout channelsList;
		TextView channelName;

		ViewHolder(View v)
		{
			super(v);
			this.v = v;
			channelsList = (LinearLayout) v.findViewById(R.id.channels_list);
			channelName = (TextView) v.findViewById(R.id.channel_name);
		}
	}
}
