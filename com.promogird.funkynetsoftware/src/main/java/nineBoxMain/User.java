package nineBoxMain;

/**
 * Created by Paul Gallini on 7/11/16.
 */
public class User {
    private String userName;
    private String userEmail;
    private int userNumber;
    private long userID;

    private  volatile User instance = this;

//    static public User getInstance() {
////        if (this.instance == null ) {
////            instance = new User();
////        }
//
//        return this;
//    }

    public User( ) {

        super();

    }

    public User( long userID ) {
        super();
        setUserID( userID );
    }
    public long getUserID() {
        return userID;
    }

    public void setUserID(long currentUserID) {
        this.userID = currentUserID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }
}
