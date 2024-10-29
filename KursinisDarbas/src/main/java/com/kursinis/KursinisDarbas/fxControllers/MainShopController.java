package com.kursinis.KursinisDarbas.fxControllers;

import com.kursinis.KursinisDarbas.hibernateControllers.CustomHib;
import com.kursinis.KursinisDarbas.model.*;
import com.kursinis.KursinisDarbas.tableviewparameters.CustomerTableParameters;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainShopController implements Initializable {

    @FXML
    public ListView<Product> productList;
    @FXML
    public ListView<Cart> currentOrder;
    @FXML
    public Tab usersTab;
    @FXML
    public Tab warehouseTab;
    @FXML
    public ListView<Warehouse> warehouseList;
    @FXML
    public TextField addressWarehouseField;
    @FXML
    public TextField titleWarehouseField;
    @FXML
    public Tab ordersTab;
    @FXML
    public Tab productsTab;

    @FXML
    public TabPane tabPane;
    @FXML
    public Tab primaryTab;
    @FXML
    public ListView<Product> productListManager;
    @FXML
    public TextField productTitleField;
    @FXML
    public TextArea productDescriptionField;
    @FXML
    public ComboBox<ProductType> productType;
    @FXML
    public ComboBox<Warehouse> warehouseComboBox;
    @FXML
    public DatePicker plantDateField;
    @FXML
    public TextField weightField;
    @FXML
    public TextArea chemicalDescriptionField;
    @FXML
    public TextField productManufacturerField;
    public TextField commentTitleField;
    public TextArea commentBodyField;
    public ListView<Comment> commentListField;
    public Tab commentTab;
    @FXML
    public ListView<Product> productsListWarehouse;

     //----------user tab
    @FXML
    public TableView customerTable;
    @FXML
    public TableView managerTable;
    @FXML
    public TableColumn<CustomerTableParameters, Integer> idTableCol;
    @FXML
    public TableColumn<CustomerTableParameters, String> loginTableCol;
    @FXML
    public TableColumn<CustomerTableParameters, String> passwordTableCol;
    @FXML
    public TableColumn<CustomerTableParameters, String> addressTableCol;
    @FXML
    public TableColumn<CustomerTableParameters, Void> dummyCol;
    @FXML
    public TextArea idTextField;
    @FXML
    public ListView<Product> currentOrders;
    @FXML
    public ListView<Product> productLists;
    @FXML
    public TextField orderIdText;
    @FXML
    public ListView reviewListField;
    @FXML
    public ListView orderListField;
    @FXML
    public Tab reviewTab;

    private ObservableList<CustomerTableParameters> data = FXCollections.observableArrayList();


    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    private CustomHib customHib;

    public void setData(EntityManagerFactory entityManagerFactory, User currentUser) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = currentUser;
        limitAccess();
        loadData();
    }

    private void loadData() {
        customHib = new CustomHib(entityManagerFactory);
        productList.getItems().clear();
        productList.getItems().addAll(customHib.getAllRecords(Product.class));
        loadOrdersList();
    }

    private void limitAccess() {
        if (currentUser.getClass() == Manager.class) {
            Manager manager = (Manager) currentUser;
            if (!manager.isAdmin()) {
                managerTable.setDisable(true);
            }
        } else if (currentUser.getClass() == Customer.class) {
            Customer customer = (Customer) currentUser;
            tabPane.getTabs().remove(usersTab);
            tabPane.getTabs().remove(warehouseTab);
        } else {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.WARNING, "WTF", "WTF", "WTF");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productType.getItems().addAll(ProductType.values());

        customerTable.setEditable(true);
        // sutvarkau kokie duomenys bus pas mus tableview celese
        idTableCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginTableCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordTableCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        //kad galeciau editint
        passwordTableCol.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordTableCol.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setPassword(event.getNewValue());
            Customer customer = CustomHib.getEntityById(Customer.class, event.getTableView().getItems().get(event.getTablePosition().getRow()).getId());
            customer.setPassword(event.getNewValue());
            CustomHib.update(customer);
        });
        addressTableCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        //sita pasibandykit patys nuo pvz auksciau



        Callback<TableColumn<CustomerTableParameters, Void>, TableCell<CustomerTableParameters, Void>> callback = param ->
        {
            final TableCell<CustomerTableParameters, Void> cell = new TableCell<>(){
                private final Button deleteButton = new Button("Delete");
                {
                    deleteButton.setOnAction(event -> {
                        CustomerTableParameters row = getTableView().getItems().get(getIndex());
                        CustomHib.delete(Customer.class, row.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setGraphic(null);
                    }else {
                        setGraphic(deleteButton);

                    }
                }
            };
            return cell;
        };

        dummyCol.setCellFactory(callback);
    }

    public void leaveComment() {
    }

    public void addToCart() {

        Product selectedProduct = productList.getSelectionModel().getSelectedItem();
        currentOrders.getItems().add(productList.getSelectionModel().getSelectedItem());
        loadOrdersList();

    }

    public void loadTabValues() {
        if (primaryTab.isSelected()){
            loadOrdersList();

        }
        else if (productsTab.isSelected()) {
            loadProductListManager();

            List<Warehouse> record = customHib.getAllRecords(Warehouse.class);
            warehouseComboBox.getItems().addAll(customHib.getAllRecords(Warehouse.class));
        } else if (warehouseTab.isSelected()) {
            loadWarehouseList();
        } else if (commentTab.isSelected()) {
            loadCommentList();
        } else if (usersTab.isSelected()) {
            loadUserTables();
        } else if (ordersTab.isSelected()) {
            loadOrderList();

        } else if (reviewTab.isSelected())
        {
            loadOrdersList();
        }
    }

    private void loadUserTables() {
        List<Customer> customerList = customHib.getAllRecords(Customer.class);
        for(Customer c :customerList) {
            CustomerTableParameters customerTableParameters = new CustomerTableParameters();
            customerTableParameters.setId(c.getId());
            customerTableParameters.setLogin(c.getLogin());
            customerTableParameters.setPassword(c.getPassword());
            customerTableParameters.setAddress(c.getAddress());
            data.add(customerTableParameters);
        }
        customerTable.setItems(data);
    }
    public void enableProductFields() {
        if (productType.getSelectionModel().getSelectedItem() == ProductType.PLANT) {

            plantDateField.setDisable(false);
            weightField.setDisable(true);
            chemicalDescriptionField.setDisable(true);
        }  else if (productType.getSelectionModel().getSelectedItem() == ProductType.FERTILIZER) {
            plantDateField.setDisable(true);
            weightField.setDisable(false);
            chemicalDescriptionField.setDisable(false);
        } else   {
            plantDateField.setDisable(true);
            weightField.setDisable(false);
            chemicalDescriptionField.setDisable(true);
        }
    }

    //----------------------Product functionality-------------------------------//

    private void loadProductListManager() {
        productListManager.getItems().clear();
        productListManager.getItems().addAll(customHib.getAllRecords(Product.class));
    }

    public void addNewProduct() {
        if (productType.getSelectionModel().getSelectedItem() == ProductType.PLANT) {
            Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
            Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        customHib.create(new Plant(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, plantDateField.getValue()));
        }
        else if(productType.getSelectionModel().getSelectedItem() == ProductType.FERTILIZER)
        {
           Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
            Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
            customHib.create(new Fertilizer(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, weightField.getText(), chemicalDescriptionField.getText()));
        }
        else{
            Warehouse selectedWarehouse = warehouseComboBox.getSelectionModel().getSelectedItem();
            Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
            customHib.create(new Other(productTitleField.getText(), productDescriptionField.getText(), productManufacturerField.getText(), warehouse, weightField.getText()));

        }

        loadProductListManager();
    }

    public void updateProduct() {
        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();

        Fertilizer Product = customHib.getEntityById(Fertilizer.class, selectedProduct.getId());
        if(Product != null) {
            productType.setValue(productType.getValue());
            Product.setWeight(Double.parseDouble(weightField.getText()));
            Product.setChemicalDescription(chemicalDescriptionField.getText());
            Product.setWarehouse(warehouseComboBox.getValue());
            Product.setTitle(productTitleField.getText());
            Product.setDescription(productDescriptionField.getText());
            Product.setManufacturer(productManufacturerField.getText());
            customHib.update(Product);
        }
        Plant Product1 = customHib.getEntityById(Plant.class, selectedProduct.getId());
        if(Product1 != null)
        {
            productType.setValue(productType.getValue());
            Product1.setPlantDate(plantDateField.getValue());
            Product1.setWarehouse(warehouseComboBox.getValue());
            Product1.setTitle(productTitleField.getText());
            Product1.setDescription(productDescriptionField.getText());
            Product1.setManufacturer(productManufacturerField.getText());
            customHib.update(Product1);
        }
        Other Product2 = customHib.getEntityById(Other.class, selectedProduct.getId());
        if(Product2 != null)
        {
            productType.setValue(productType.getValue());
            Product2.setWeight(Double.parseDouble(weightField.getText()));
            Product2.setWarehouse(warehouseComboBox.getValue());
            Product2.setTitle(productTitleField.getText());
            Product2.setDescription(productDescriptionField.getText());
            Product2.setManufacturer(productManufacturerField.getText());
            customHib.update(Product2);
        }
        loadProductListManager();

    }

    public void deleteProduct() {

        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        customHib.deleteProduct(selectedProduct.getId());
        loadProductListManager();
    }


    public void loadProductInfo(MouseEvent mouseEvent) {

        Product selectedProduct = productListManager.getSelectionModel().getSelectedItem();
        Fertilizer Product = customHib.getEntityById(Fertilizer.class, selectedProduct.getId());
        if(Product != null) {
            productType.setValue(ProductType.FERTILIZER);
            weightField.setText(String.valueOf(Product.getWeight()));
            chemicalDescriptionField.setText(Product.getChemicalDescription());
            warehouseComboBox.setValue(Product.getWarehouse());
            productTitleField.setText(Product.getTitle());
            productDescriptionField.setText(Product.getDescription());
            productManufacturerField.setText(Product.getManufacturer());
        }
        Plant Product1 = customHib.getEntityById(Plant.class, selectedProduct.getId());
        if(Product1 != null)
        {
            productType.setValue(ProductType.PLANT);
            plantDateField.setValue(Product1.getPlantDate());
            warehouseComboBox.setValue(Product1.getWarehouse());
            productTitleField.setText(Product1.getTitle());
            productDescriptionField.setText(Product1.getDescription());
            productManufacturerField.setText(Product1.getManufacturer());
        }
        Other Product2 = customHib.getEntityById(Other.class, selectedProduct.getId());
        if(Product2 != null)
        {
            productType.setValue(ProductType.OTHER);
            weightField.setText(String.valueOf(Product2.getWeight()));
            warehouseComboBox.setValue(Product2.getWarehouse());
            productTitleField.setText(Product2.getTitle());
            productDescriptionField.setText(Product2.getDescription());
            productManufacturerField.setText(Product2.getManufacturer());
        }


    }

    //----------------------Warehouse functionality-----------------------------//

    private void loadWarehouseList() {
        warehouseList.getItems().clear();
        warehouseList.getItems().addAll(customHib.getAllRecords(Warehouse.class));
    }

    public void addNewWarehouse() {
        customHib.create(new Warehouse(titleWarehouseField.getText(), addressWarehouseField.getText()));
        loadWarehouseList();
    }

    public void updateWarehouse() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        warehouse.setTitle(titleWarehouseField.getText());
        warehouse.setAddress(addressWarehouseField.getText());
        customHib.update(warehouse);
        loadWarehouseList();
    }

    public void removeWarehouse() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        Warehouse warehouse = customHib.getEntityById(Warehouse.class, selectedWarehouse.getId());
        customHib.delete(Warehouse.class, selectedWarehouse.getId());
        loadWarehouseList();
    }

    public void loadWarehouseData() {
        Warehouse selectedWarehouse = warehouseList.getSelectionModel().getSelectedItem();
        titleWarehouseField.setText(selectedWarehouse.getTitle());
        addressWarehouseField.setText(selectedWarehouse.getAddress());
        productsListWarehouse.getItems().clear();
        productsListWarehouse.getItems().addAll(selectedWarehouse.getInStockProducts());
    }


    //--------------Comment test section ------------------------//

    private void loadCommentList() {
        commentListField.getItems().clear();
        commentListField.getItems().addAll(customHib.getAllRecords(Comment.class));
    }

    public void createComment() {
        Comment comment = new Comment(commentTitleField.getText(), commentBodyField.getText());
        customHib.create(comment);
        loadCommentList();
    }


    public void updateComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        Comment commentFromDb = customHib.getEntityById(Comment.class, selectedComment.getId());
        commentFromDb.setCommentTitle(commentTitleField.getText());
        commentFromDb.setCommentBody(commentBodyField.getText());
        customHib.update(commentFromDb);
        loadCommentList();
    }

    public void deleteComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        //Comment commentFromDb = genericHib.getEntityById(Comment.class, selectedComment.getId());
        customHib.delete(Comment.class, selectedComment.getId());
        loadCommentList();
    }

    public void loadCommentInfo() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        commentTitleField.setText(selectedComment.getCommentTitle());
        commentBodyField.setText(selectedComment.getCommentBody());
    }

    public void addOrder(ActionEvent actionEvent) {
        Cart cart = new Cart();
        customHib.create(cart);
        loadOrderList();
    }

    private void loadOrderList() {
        customHib = new CustomHib(entityManagerFactory);
        currentOrder.getItems().clear();
        currentOrder.getItems().addAll(customHib.getAllRecords(Cart.class));

    }

    public void removeOrder() {
        Cart selectedCart = currentOrder.getSelectionModel().getSelectedItem();
        Cart cart = customHib.getEntityById(Cart.class, selectedCart.getId());
        customHib.delete(Cart.class, selectedCart.getId());
        loadOrderList();
    }

    private void loadOrdersList() {
        customHib = new CustomHib(entityManagerFactory);


    }
    @FXML
    private void loadOrdersLists() {
        customHib = new CustomHib(entityManagerFactory);

//        customHib.getEntityById(Cart.class, selectedCart.getId();
        productLists.getItems().clear();
//        currentOrder.getItems().addAll(Product.get);
        Cart selectedcart = currentOrder.getSelectionModel().getSelectedItem();
        orderIdText.setText(String.valueOf(selectedcart.getId()));
        productLists.getItems().addAll(selectedcart.getItemsInCart());
    }



    public void updateOrder(ActionEvent actionEvent) {
        Cart selectedcart = currentOrder.getSelectionModel().getSelectedItem();
        selectedcart.setId(Integer.parseInt(orderIdText.getText()));
        loadOrderList();
    }

    public void addCarttoOrder(ActionEvent actionEvent) {
        customHib = new CustomHib(entityManagerFactory);
        new Cart(currentOrders.getItems());
        currentOrders.getItems().clear();

    }

    public void removeProduct(ActionEvent actionEvent) {
        currentOrders.getItems().remove(currentOrders.getSelectionModel().getSelectedItem());
    }
    //------current order------//
    

}
