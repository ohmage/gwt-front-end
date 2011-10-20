package edu.ucla.cens.mobilize.client;


/**
 * An interface for a set of classes which are used to choose the deployment
 * status.  This is so the status can be switched from debug and release in the
 * module xml file.
 */
public interface DeployStatus {
    public enum Status { RELEASE, DEBUG };
    
    /**
     * Returns Status.DEBUG.
     */
    public class Debug implements DeployStatus {
      public Status getStatus() {
        return Status.DEBUG;
      }
    }

    /**
     * Returns Status.RELEASE
     */
    public class Release implements DeployStatus {
      public Status getStatus() {
        return Status.RELEASE;
      }
    }

    Status getStatus();
}
