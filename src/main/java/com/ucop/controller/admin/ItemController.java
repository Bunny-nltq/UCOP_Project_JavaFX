package com.ucop.controller.admin;

import com.ucop.entity.Item;
import com.ucop.service.ItemService;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.math.BigDecimal;

public class ItemController {

    @FXML private TextField txtSku, txtName, txtPrice, txtStock;
    @FXML private TableView<Item> tbl;
    @FXML private TableColumn<Item, Integer> colId;
    @FXML private TableColumn<Item, String> colSku;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, BigDecimal> colPrice;
    @FXML private TableColumn<Item, Integer> colStock;

    private final ItemService service = new ItemService();

    @FXML
    public void initialize(){
        refresh();
    }

    private void refresh(){
        tbl.setItems(FXCollections.observableArrayList(service.findAll()));

        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colSku.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getSku()));
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPrice()));
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStockQty()));
    }

    @FXML
    public void add(ActionEvent e){
        try{
            Item it = new Item();
            it.setSku(txtSku.getText());
            it.setName(txtName.getText());
            it.setPrice(new BigDecimal(txtPrice.getText()));
            it.setStockQty(Integer.parseInt(txtStock.getText()));

            service.save(it);

            txtSku.clear(); txtName.clear();
            txtPrice.clear(); txtStock.clear();

            refresh();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
