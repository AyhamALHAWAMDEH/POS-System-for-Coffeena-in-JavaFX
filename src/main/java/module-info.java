module com.dynamo.devs.psoc.pos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires itextpdf;

    opens com.dynamo.devs.psoc.pos to javafx.fxml;
    exports com.dynamo.devs.psoc.pos;
}