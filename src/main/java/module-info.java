module com.kmidiplayer {
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

    // logger
    requires transitive org.apache.logging.log4j;

    // other
    opens com.kmidiplayer.gui to javafx.fxml;
    exports com.kmidiplayer.gui to javafx.graphics;
    exports com.kmidiplayer.keylogger to com.kmidiplayer;
    exports com.kmidiplayer.application;
}
