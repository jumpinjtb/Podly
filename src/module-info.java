module Podly {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.base;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.fxml;
    requires jdom;

    opens Main;
    opens Podcast;
    opens Search;
    exports Podcast;
    exports Main;
    exports Search;
}