package cz.jiriskorpil.amixerwebui.control.mixer;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.IBooleanControl;

/**
 * Represents boolean control (on/off).
 */
public class BooleanControl extends MixerControl implements IBooleanControl
{
	private boolean value;

	/**
	 * Creates new instance of boolean control.
	 *
	 * @param jsonObject JSON object representing boolean control retrieved from server
	 * @throws JSONException
	 */
	public BooleanControl(JSONObject jsonObject) throws JSONException
	{
		super(jsonObject);
		value = (boolean) jsonObject.getJSONArray("values").get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isChecked()
	{
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setChecked(boolean value)
	{
		this.value = value;
	}
}
