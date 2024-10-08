module com.kmidiplayer {
    // jfx
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;

    // mfx
    requires MaterialFX;

    // jna
    requires transitive com.sun.jna;
    requires transitive com.sun.jna.platform;

    // json
    requires com.fasterxml.jackson.databind;

    // yaml
    requires org.yaml.snakeyaml;

    // midi
    requires java.desktop;

    // logger
    requires transitive org.apache.logging.log4j;
    requires javafx.base;

    // other
    opens com.kmidiplayer.gui to javafx.fxml;
    exports com.kmidiplayer.gui to javafx.graphics, io.github.palexdev.materialfx.graphics;
    exports com.kmidiplayer.keylogger to com.kmidiplayer;
    exports com.kmidiplayer.application;
}
