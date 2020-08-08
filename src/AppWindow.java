import javafx.application.Application;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//import org.w3c.dom.Text;
import passwordGenerator.PasswordGenerator;
import PasswordDatabase.sqlConnection;

import javax.swing.*;
import java.sql.*;
import java.util.*;




public class AppWindow extends Application{

    private Stage window;
    private Scene passwordScene, settingsScene, getPasswordScene;
    private ObservableList<String> websiteList, userNameList = FXCollections.observableArrayList();
    private ComboBox<String> websiteSelectionDropDown, userNameSelectionDropDown;
    private TextField showPassword;
    private boolean showPasswordToggle = true;

    public static void main(String[] args) {
        launch(args);

    }

    public void start(Stage primaryStage) throws Exception{

        PasswordGenerator password = new PasswordGenerator(15);


        window = primaryStage;



        // #######################################################
        // ############### Password Creation scene 1 #############
        // #######################################################

        GridPane passwordGrid = new GridPane();
        passwordGrid.setPadding(new Insets(30, 30, 30, 30));
        passwordGrid.setVgap(5);
        passwordGrid.setHgap(5);

        Label introLabel = new Label("Please provide the user data below: ");
        introLabel.setPadding(new Insets(0, 0, 30, 0));
        GridPane.setConstraints(introLabel, 0, 0);

        Label siteLabel = new Label("Website: ");
        GridPane.setConstraints(siteLabel, 0, 2);

        TextField siteInput = new TextField();
        GridPane.setConstraints(siteInput, 1, 2);

        Label userLabel = new Label("Username: ");
        GridPane.setConstraints(userLabel, 2, 2);

        TextField userInput = new TextField();
        GridPane.setConstraints(userInput, 3, 2);

        Label passwordLabel = new Label("Password: ");
        GridPane.setConstraints(passwordLabel, 4, 2);

        TextField passwordInput = new TextField("Default Password");
        GridPane.setConstraints(passwordInput, 5, 2);

        //Generate new random password
        Button generatePassword = new Button("Generate New");
        GridPane.setConstraints(generatePassword, 6, 2);
        generatePassword.setOnAction(e -> {
            String newPassword = password.generateRandomPassword();
            passwordInput.setText(newPassword);
        });

        //Save user entry to DB
        Button savePassword = new Button("Save User Login Data");
        GridPane.setConstraints(savePassword, 3, 3);
        GridPane.setMargin(savePassword, new Insets(50,0,0,0));
        savePassword.setOnAction(e -> {
            //List <String> userInputs = getUserInput(siteInput, userInput, passwordInput);
            if(userInput.getText().length() != 0 && siteInput.getText().length() != 0 && passwordInput.getText().length() != 0){
                List <String> userInputs = getUserInput(siteInput, userInput, passwordInput);
                sqlConnection inputConnection = new sqlConnection();
                inputConnection.saveUserInput(userInputs.get(0), userInputs.get(1), userInputs.get(2));
                System.out.println(getUserInput(siteInput, userInput, passwordInput));
                inputConnection.closeConnection();
                updateWebsiteList(websiteSelectionDropDown);
                siteInput.setText(null);
                userInput.setText(null);
                passwordInput.setText(null);
            }
            else{
                JOptionPane.showMessageDialog(null, "Please provide input for all fields!");
            }
        });

        //Go to getPasswordScene
        Button getPassword = new Button("Manage Passwords");
        GridPane.setConstraints(getPassword, 0, 4);
        GridPane.setMargin(getPassword, new Insets(0,0,0,0));
        getPassword.setOnAction(e -> window.setScene((getPasswordScene)));

        //Go to settingsScene
        Button goToSettings = new Button("Settings");
        GridPane.setConstraints(goToSettings, 0, 5);
        GridPane.setMargin(goToSettings, new Insets(0,0,0,0));
        goToSettings.setOnAction(e -> window.setScene(settingsScene));

        Button dbSettings = new Button("Database Settings");
        GridPane.setConstraints(dbSettings, 0, 6);
        GridPane.setMargin(dbSettings, new Insets(0,0,0,0));




        passwordGrid.getChildren().addAll(siteLabel, siteInput, userLabel, userInput,
                passwordLabel, passwordInput, savePassword, introLabel, generatePassword,
                goToSettings, dbSettings, getPassword);
        passwordScene = new Scene(passwordGrid, 1000, 400);

        // #######################################################
        // ################### Settings scene 2 ##################
        // #######################################################
        GridPane settingsGrid = new GridPane();
        settingsGrid.setPadding(new Insets(30, 30, 30, 30));
        settingsGrid.setVgap(5);
        settingsGrid.setHgap(5);


        // Current parameters section
        Label passwordLenght = new Label("Current Password Length:");
        GridPane.setConstraints(passwordLenght, 0, 1);

        Text currentPasswordLength = new Text();
        currentPasswordLength.setText(String.valueOf(password.getPasswordLength()));
        GridPane.setConstraints(currentPasswordLength, 1, 1);

        // Set new parameters section
        Label setNewLengthLabel = new Label("Set Up New Password Length:");
        GridPane.setConstraints(setNewLengthLabel, 0, 2);

        TextField setNewLengthInput = new TextField();
        GridPane.setConstraints(setNewLengthInput, 1, 2);

        Text saveLengthOutput = new Text();
        GridPane.setConstraints(saveLengthOutput, 0, 3);

        Button saveNewLenght = new Button("Save New Length");
        GridPane.setConstraints(saveNewLenght, 2, 2);
        saveNewLenght.setOnAction(e -> {
            String message = newLength(setNewLengthInput, password);
            saveLengthOutput.setText(message);
            currentPasswordLength.setText(String.valueOf(password.getPasswordLength()));
        });


        // Navigation buttons
        Button goToPassword = new Button("Password Scene");
        GridPane.setConstraints(goToPassword, 0, 4);
        goToPassword.setOnAction(e -> window.setScene(passwordScene));

        settingsGrid.getChildren().addAll(goToPassword, passwordLenght, currentPasswordLength,
                setNewLengthLabel, setNewLengthInput, saveNewLenght, saveLengthOutput);

        settingsScene = new Scene(settingsGrid, 1000, 400);

        // #######################################################
        // ############## Retrieve Password scene 3 ##############
        // #######################################################

        GridPane getPasswordGrid = new GridPane();
        getPasswordGrid.setPadding(new Insets(30, 30, 30, 30));
        getPasswordGrid.setVgap(5);
        getPasswordGrid.setHgap(5);



        Label getPasswordLabel = new Label("Select website and user to interact with");
        GridPane.setConstraints(getPasswordLabel, 0, 1);

        //List of websites with saved passwords
        websiteSelectionDropDown = new ComboBox<>();
        updateWebsiteList(websiteSelectionDropDown);
        websiteSelectionDropDown.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> updateUserNameList(userNameSelectionDropDown, newValue));
        GridPane.setConstraints(websiteSelectionDropDown, 1, 3);

        //Filter passwords on website selection
        Button filterOnWebsite = new Button("Filter Entries");
        GridPane.setConstraints(filterOnWebsite, 2, 3);

        //Select username to show password for
        userNameSelectionDropDown = new ComboBox<>();
        //userNameSelectionDropDown.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> showPassword(showPassword, websiteSelectionDropDown, newValue));
        GridPane.setConstraints(userNameSelectionDropDown, 3, 3);

        showPassword = new TextField();
        GridPane.setConstraints(showPassword, 4, 3);

        //Query password with selected parameters
        Button queryPassword = new Button("Show Password");
        GridPane.setConstraints(queryPassword, 5, 3);
        queryPassword.setOnAction(e -> {
            if(showPasswordToggle){
                showPasswordToggle = false;
                showPassword(showPassword, websiteSelectionDropDown, userNameSelectionDropDown.getValue());
                queryPassword.setText("Hide Password");
            }
            else{
                showPassword.setText("");
                queryPassword.setText("Show Password");
                showPasswordToggle = true;
            }
        });

        //Modify password label
        Label modifyPasswordLabel = new Label("Enter new password to overwrite the existing one");
        GridPane.setConstraints(modifyPasswordLabel, 0, 4);

        //Modify password entry field
        TextField modifyPasswordEntry = new TextField();
        GridPane.setConstraints(modifyPasswordEntry, 4, 5);

        //Modify password save new entry button
        Button saveModifiedPassword = new Button("Save Modified Password");
        GridPane.setConstraints(saveModifiedPassword, 5, 5);





        // Go to Password Input
        Button goToPassword2 = new Button("Password Scene");
        GridPane.setConstraints(goToPassword2, 0, 10);
        goToPassword2.setOnAction(e -> window.setScene(passwordScene));


        getPasswordGrid.getChildren().addAll(goToPassword2, queryPassword, websiteSelectionDropDown, filterOnWebsite,
                userNameSelectionDropDown, showPassword, getPasswordLabel, modifyPasswordLabel, modifyPasswordEntry,
                saveModifiedPassword);
        getPasswordScene = new Scene(getPasswordGrid, 1000, 400);





        window.setScene(passwordScene);
        window.setTitle("Password generator");
        window.show();
    }

    //Save user password input after pushing savePassword button
    private static List<String> getUserInput(TextField input1, TextField input2, TextField input3){
        if(input1.getText().isEmpty() || input2.getText().isEmpty() || input3.getText().isEmpty()){
            System.out.println("Please provide all inputs");
            return null;
        } else {
            String website = input1.getText();
            String username = input2.getText();
            String password = input3.getText();
            return Arrays.asList(website, username, password);
        }
    }

    private static String newLength(TextField lengthInput, PasswordGenerator generatorInstance){
        String returnMessage = new String();
        try{
            int newLength = Integer.parseInt(lengthInput.getText());
            generatorInstance.setPasswordLength(newLength);
            returnMessage = "New Length Saved";

        }catch (NumberFormatException e){
            returnMessage = "Invalid Integer Input";
        }
        return returnMessage;
    }

    //Update the list of websites in websiteSelectionDropDown from the DB
    private void updateWebsiteList(ComboBox<String> choiceBox){
        try{
            sqlConnection connection = new sqlConnection();
            websiteList = connection.queryWebsites();
            connection.closeConnection();
            choiceBox.setItems(websiteList);
        }
        catch (Exception e){
            System.out.println("updateWebsiteList Error: " + e.getMessage());
        }
    }

    //Update the usernames in userNameSelectionDropDown based on website selection
    private void updateUserNameList(ComboBox<String> comboBox, String website){
        try {
            sqlConnection connection = new sqlConnection();
            userNameList = connection.queryUserNames(website);
            connection.closeConnection();
            comboBox.setItems(userNameList);
        }
        catch (Exception e){
            System.out.println("updateUserNameList Error: " + e.getMessage());
        }
    }

    //Get the website name and username data from getPasswordScene to query the password associated with them
    private void showPassword(TextField passwordOutput, ComboBox websiteComboBox, String userName){
        String website = (String) websiteComboBox.getValue();
        try{
            sqlConnection connection = new sqlConnection();
            String password = connection.queryPasswordByWebsite(website, userName);
            connection.closeConnection();
            passwordOutput.setText(password);
        }
        catch (Exception e){
            System.out.println("showPassword Error: " + e.getMessage());
        }
    }

}
