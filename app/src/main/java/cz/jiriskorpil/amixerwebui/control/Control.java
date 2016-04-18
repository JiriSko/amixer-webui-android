package cz.jiriskorpil.amixerwebui.control;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents one control.
 */
public class Control implements IControl
{
	private int id;
	private InterfaceType iface;
	private String access;
	private String name;

	/**
	 * Creates new instance of control.
	 *
	 * @param jsonObject JSON object representing control retrieved from server
	 * @throws JSONException
	 */
	public Control(JSONObject jsonObject) throws JSONException
	{
		super();
		id = jsonObject.getInt("id");
		iface = InterfaceType.fromString(jsonObject.getString("iface"));
		access = jsonObject.getString("access");
		name = jsonObject.getString("name");
	}

	/**
	 * {@inheritDoc}
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public InterfaceType getIface()
	{
		return iface;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAccess()
	{
		return access;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return name;
	}
}
