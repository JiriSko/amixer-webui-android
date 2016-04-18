package cz.jiriskorpil.amixerwebui.control.mixer;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.IChannel;
import cz.jiriskorpil.amixerwebui.control.IIntegerControl;

/**
 * Represents integer control (common volume slider).
 */
public class IntegerControl extends MixerControl implements IIntegerControl
{
	private IChannel[] channels;

	private int min;
	private int max;
	private int step;

	/**
	 * Creates new instance of integer control.
	 *
	 * @param jsonObject JSON object representing integer control retrieved from server
	 * @throws JSONException
	 */
	public IntegerControl(JSONObject jsonObject) throws JSONException
	{
		super(jsonObject);

		min = jsonObject.getInt("min");
		max = jsonObject.getInt("max");
		step = jsonObject.getInt("step");

		channels = new Channel[jsonObject.getJSONArray("channels").length()];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = new Channel(this,
					jsonObject.getJSONArray("channels").get(i).toString(),
					Integer.parseInt(jsonObject.getJSONArray("values").get(i).toString()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IChannel[] getChannels()
	{
		return channels;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMin()
	{
		return min;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getStep()
	{
		return step;
	}
}
