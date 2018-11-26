package nspirep2p.application.server.database;

public enum Permission {
    //Standard user permissions
    READ_CHANNEL("Sorry, you are not allowed to see normal channel. Try creating your own."),
    CREATE_TEMP_CHANNEL("Sorry, you are not allowed to create your own Channel, use public instead."),
    //Moderator permissions
    MANAGE_PUBLIC_CHANNEL,
    KICK_USER,
    //Admin permissions
    READ_UUID,
    CONTROL_OTHER("Please do not pretend to say, that you are a different one.");

    private String noPermError;
    Permission(String noPermError){
        this.noPermError = noPermError;
    }
    Permission(){
        noPermError = "Sorry, you do not have permission";
    }

    public String getNoPermissionError() {
        return noPermError;
    }
}
