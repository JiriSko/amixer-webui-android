package cz.jiriskorpil.amixerwebui.control.mixer;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.IEnumeratedControl;

/**
 * Represents enumerated control (select one from many).
 */
public class EnumeratedControl extends MixerControl implements IEnumeratedControl
{
	private String[] items;
	private int value;

	/**
	 * Creates new instance of enumerated control.
	 *
	 * @param jsonObject JSON object representing enumerated control retrieved from server
	 * @throws JSONException
	 */
	public EnumeratedControl(JSONObject jsonObject) throws JSONException
	{
		super(jsonObject);

		value = (int) jsonObject.getJSONArray("values").get(0);

		items = new String[jsonObject.getJSONObject("items").length()];
		for (int i = 0; i < items.length; i++) {
			items[i] = jsonObject.getJSONObject("items").getString(String.valueOf(i));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getItems()
	{
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(int value)
	{
		this.value = value;
	}
}
