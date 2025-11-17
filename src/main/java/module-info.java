module org.example.editorapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.fxmisc.richtext;
    requires java.desktop;
    requires org.commonmark;


    opens org.example.editorapp to javafx.fxml;
    exports org.example.editorapp;
}