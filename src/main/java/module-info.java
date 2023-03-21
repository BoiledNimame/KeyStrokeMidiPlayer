module com.ckmidi {
    // jfx
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    
    // jna
    requires transitive com.sun.jna;
    requires transitive com.sun.jna.platform;

    // json
    requires com.fasterxml.jackson.databind;

    // midi
    requires java.desktop;

    // other
    opens com.kmidiplayer to javafx.fxml;
    exports com.kmidiplayer;
}
