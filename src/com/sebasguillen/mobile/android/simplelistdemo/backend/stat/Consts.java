package com.sebasguillen.mobile.android.simplelistdemo.backend.stat;

/**
 * The Constants class
 * Class full of string constants that will be used internally, like database column names or other keys.
 * Note: strings.xml should have the strings that are displayed for the user. This way you can take advantage of localization, etc.
 * @author Sebastian Guillen
 */
public final class Consts {

	private Consts() {
		// Hide Utility Class Constructor
	}

	public static final String EMPTY_STRING = "";
	public static final String SINGLE_SPACE = " ";
	public static final String SINGLE_ENTER = "\n";
	public static final String APOSTROPHE = "\"";

	//Formats
	public static final String PLAIN_TEXT = "text/plain";

	//Tags
	public static final String NO_TAG = "No Tag";

	//Predefined strings
	public static final String GEO = "geo:0,0?q=";


	//Key to Bundle Extras
	//Used to send info from one activity to another
	//be = bundle extra
	//The URL to use
	public static final String BE_INTENT_TASK_ID_KEY = "BE_INTENT_TASK_ID_KEY";
	public static final String BE_INTENT_MARK_TASK_AS_COMPLETE = "BE_INTENT_MARK_TASK_AS_COMPLETE";

}
