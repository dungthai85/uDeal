package model;

import java.io.Serializable;

/**
 * This class is used for emailing when the user
 * changes their password.
 *
 * @author TCSS 450 Team 8
 * @version 1.0
 */
public class NewPassEmail implements Serializable {

    /** Used to retrieve User Name. */
    public static final String USER_NAME = "user_name";

    /** Used to retrieve User Name. */
    public static final String USER_EMAIL = "user_email";

    /** The user Name. */
    private String mUserName;

    /** The user Email. */
    private String mFNameEmail;

    /**
     * Initializes the fields of the NewPassEmail object.
     *
     * @param theUserName The user name
     * @param theEmail The user email
     */
    public NewPassEmail(String theUserName, String theEmail) {
        mUserName = theUserName;
        mFNameEmail = theEmail;
    }

    /**
     * Gets the member name.
     *
     * @return The member name
     */
    public String getmUserName() {
        return mUserName;
    }

    /**
     * Gets the email.
     *
     * @return The email
     */
    public String getmEmail() {
        return mFNameEmail;
    }
}
