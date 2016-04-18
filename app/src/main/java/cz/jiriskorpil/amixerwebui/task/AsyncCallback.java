package cz.jiriskorpil.amixerwebui.task;

/**
 * Interface for representing callback after asynchronous task finished.
 */
public interface AsyncCallback
{
	/**
	 * Method which is executed after asynchronous task finished.
	 * @param result downloaded data
	 */
	void execute(String result);
}
