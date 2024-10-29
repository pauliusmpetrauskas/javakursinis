package com.kursinis.KursinisDarbas.fxControllers;

import com.kursinis.KursinisDarbas.StartGui;
import com.kursinis.KursinisDarbas.hibernateControllers.UserHib;
import com.kursinis.KursinisDarbas.model.Customer;
import com.kursinis.KursinisDarbas.model.Manager;
import com.kursinis.KursinisDarbas.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public RadioButton customerCheckbox;
    @FXML
    public ToggleGroup userType;
    @FXML
    public RadioButton managerCheckbox;
    @FXML
    public TextField addressField;
    @FXML
    public TextField cardNoField;
    @FXML
    public DatePicker birthDateField;
    @FXML
    public TextField employeeIdField;
    @FXML
    public TextField medCertificateField;
    @FXML
    public DatePicker employmentDateField;
    @FXML
    public CheckBox isAdminCheck;

    private EntityManagerFactory entityManagerFactory;
    private UserHib userHib;

    public void setData(EntityManagerFactory entityManagerFactory, boolean showManagerFields) {
        this.entityManagerFactory = entityManagerFactory;
        disableFields(showManagerFields);
    }

    public void setData(EntityManagerFactory entityManagerFactory, Manager manager) {
        this.entityManagerFactory = entityManagerFactory;
        toggleFields(customerCheckbox.isSelected(), manager);
    }

    private void disableFields(boolean showManagerFields) {
        if (!showManagerFields) {
            employeeIdField.setVisible(false);
            medCertificateField.setVisible(false);
            employmentDateField.setVisible(false);
            isAdminCheck.setVisible(false);
            managerCheckbox.setVisible(false);
        }
    }

    private void toggleFields(boolean isManager, Manager manager) {
        if (isManager) {
            addressField.setDisable(true);
            cardNoField.setDisable(true);
            employeeIdField.setDisable(false);
            medCertificateField.setDisable(false);
            employmentDateField.setDisable(false);
            if (manager.isAdmin()) isAdminCheck.setDisable(false);
        } else {
            addressField.setDisable(false);
            cardNoField.setDisable(false);
            employeeIdField.setDisable(true);
            medCertificateField.setDisable(true);
            employmentDateField.setDisable(true);
            isAdminCheck.setDisable(true);
        }
    }


    public void createUser() {
        userHib = new UserHib(entityManagerFactory);
        if (Objects.equals(loginField.getText(), "") )
        {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.WARNING, "error", "error", "you need to enter loginfield ");
            return;
        }
        if (Objects.equals(passwordField.getText(), "") )
        {
            JavaFxCustomUtils.generateAlert(Alert.AlertType.WARNING, "error", "error", "you need to enter password");
            return;
        }
        if (customerCheckbox.isSelected()) {
            User user = new Customer(loginField.getText(), passwordField.getText(), birthDateField.getValue(), nameField.getText(), surnameField.getText(), addressField.getText(), cardNoField.getText());
            userHib.createUser(user);
        } else {
            User user = new Manager(loginField.getText(),passwordField.getText(),birthDateField.getValue(),nameField.getText(),surnameField.getText(),employeeIdField.getText(),medCertificateField.getText(),employmentDateField.getValue(),isAdminCheck.isSelected());
            userHib.createUser(user);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Arba cia kazka su laukais darau
    }


    public void backtologin(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
        Parent parent = fxmlLoader.load();
        //Po sios dalies as galiu pasiekti kontrolerius
        LoginController LoginController = fxmlLoader.getController();
        LoginController.setData(entityManagerFactory);
        Scene scene = new Scene(parent);
        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
