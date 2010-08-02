package edu.ucla.cens.AndWellnessVisualizations.client.model;


/**
 * Storage class to hold basic user information.
 * 
 * @author jhicks
 *
 */
public class UserInfo implements Comparable<UserInfo> {
    public String userName;
    public int privileges;
    
    public UserInfo() {};
    
    public UserInfo(String userName) {
        this.userName = userName;
        // We do not know the privileges, set to invalid
        this.privileges = -1;
    }
    
    public UserInfo(String userName, int privileges) {
        this.userName = userName;
        this.privileges = privileges;
    }
    
    // Return whether the user is an admin
    public boolean isAdmin() {
        if (privileges == 1) {
            return true;
        }
        
        return false;
    }
    
    // Return whether the user is a researcher
    public boolean isResearcher() {
        if (privileges == 3) {
            return true;
        }
        
        return false;
    }
    
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getPrivileges() { return privileges; }
    public void setPrivileges(int privileges) { this.privileges = privileges; }

    // Allows this model to be sorted by Collections.sort() (be userName only)
    @Override
    public int compareTo(UserInfo arg0) {
        return this.userName.compareTo(arg0.userName);
    }
}
