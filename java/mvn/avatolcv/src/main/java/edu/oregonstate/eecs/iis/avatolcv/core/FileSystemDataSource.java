package edu.oregonstate.eecs.iis.avatolcv.core;

public class FileSystemDataSource implements DataSource {

    @Override
    public boolean authenticate(String username, String password) {
        // nothing to authenticate
        return true;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

}
