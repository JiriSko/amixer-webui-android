package cz.jiriskorpil.amixerwebui.control.mixer;

import cz.jiriskorpil.amixerwebui.control.IChannel;
import cz.jiriskorpil.amixerwebui.control.IIntegerControl;

/**
 * Represents one channel in integer control.
 */
public class Channel implements IChannel
{
	private IIntegerControl control;
	private String name;
	private int value;

	/**
	 * Creates new channel instance.
	 *
	 * @param control parent control
	 * @param name    channel name
	 * @param value   channel value
	 */
	public Channel(IIntegerControl control, String name, int value)
	{
		this.control = control;
		this.name = name;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public IIntegerControl getControl()
	{
		return control;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
	{
		return name;
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
