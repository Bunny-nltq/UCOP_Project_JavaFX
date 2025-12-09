package com.ucop.controller.admin;

import java.net.URL;
import java.util.ResourceBundle;

import com.ucop.dao.AuditLogDAO;
import com.ucop.entity.AuditLog;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AuditLogController implements Initializable {

    @FXML private TableView<AuditLog> table;
    @FXML private TableColumn<AuditLog, String> colEntity;
    @FXML private TableColumn<AuditLog, Long> colEntityId;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colActor;
    @FXML private TableColumn<AuditLog, String> colTime;

    private AuditLogDAO dao = new AuditLogDAO();

    @Override
    public void initialize(URL loc, ResourceBundle rb) {
        colEntity.setCellValueFactory(new PropertyValueFactory<>("entityName"));
        colEntityId.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colActor.setCellValueFactory(new PropertyValueFactory<>("actor"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        table.setItems(FXCollections.observableList(dao.getAll()));
    }
}
