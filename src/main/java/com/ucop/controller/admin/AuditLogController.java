package com.ucop.controller.admin;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.ucop.dao.AuditLogDAO;
import com.ucop.entity.AuditLog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AuditLogController implements Initializable {

    @FXML private TableView<AuditLog> table;

    // FULL COLUMNS
    @FXML private TableColumn<AuditLog, Long> colId;
    @FXML private TableColumn<AuditLog, Long> colUserId;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colDetails;
    @FXML private TableColumn<AuditLog, Long> colEntityId;
    @FXML private TableColumn<AuditLog, String> colEntityName;
    @FXML private TableColumn<AuditLog, String> colNewValue;
    @FXML private TableColumn<AuditLog, String> colOldValue;
    @FXML private TableColumn<AuditLog, LocalDateTime> colTime;

    @FXML private Button btnDelete;
    @FXML private Label lblMsg;

    private final AuditLogDAO dao = new AuditLogDAO();
    private ObservableList<AuditLog> logs;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // MAP PROPERTY TƯƠNG ỨNG VỚI AuditLog.java
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colEntityId.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        colEntityName.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // FORMAT DATE
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colTime.setCellFactory(column -> new TableCell<AuditLog, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : fmt.format(item));
            }
        });

        loadLogs();
    }


    /** LOAD FULL DATA */
    private void loadLogs() {
        logs = FXCollections.observableArrayList(dao.getAll());
        table.setItems(logs);
    }


    /** DELETE SELECTED LOG */
    @FXML
    private void handleDelete() {
        AuditLog selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            lblMsg.setText("Select a log to delete!");
            return;
        }

        try {
            dao.delete(selected.getId());
            lblMsg.setText("Deleted log ID: " + selected.getId());
            loadLogs();

        } catch (Exception e) {
            lblMsg.setText("Error deleting log!");
            e.printStackTrace();
        }
    }
}
