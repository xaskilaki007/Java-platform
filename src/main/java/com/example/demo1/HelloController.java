
package com.example.demo1;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;

public class HelloController {
    @FXML
    private TableView<String[]> myTableView;
    @FXML
    private TableColumn<String[], String> authorColumn;
    @FXML
    private TableColumn<String[], String> bookColumn;
    @FXML
    private TableColumn<String[], String> yearColumn;
    @FXML
    private TableColumn<String[], String> pagesColumn;
    @FXML
    private TableColumn<String[], String> publisherColumn;
    @FXML
    private RadioButton showTableRadioButton;
    @FXML
    private Button importButton;
    @FXML
    private Button exportButton;
    @FXML
    private MenuButton actionMenuButton;
    @FXML
    private MenuItem editMenuItem;
    @FXML
    private MenuItem insertMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private PieChart pagesPieChart;

    private ObservableList<String[]> tableData = FXCollections.observableArrayList();
    private File selectedFile;

    @FXML
    public void initialize() {
        // Настройка отображения данных в столбцах
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        bookColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        yearColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        pagesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
        publisherColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));

        // Настройка редактирования для столбцов
        myTableView.setEditable(true);
        authorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        bookColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        yearColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        pagesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        publisherColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Настройка обработчиков для изменения значений ячеек
        authorColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            tableData.get(row)[0] = event.getNewValue();
            updatePieChart();
        });
        bookColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            tableData.get(row)[1] = event.getNewValue();
            updatePieChart();
        });
        yearColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            tableData.get(row)[2] = event.getNewValue();
            updatePieChart();
        });
        pagesColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            tableData.get(row)[3] = event.getNewValue();
            updatePieChart();
        });
        publisherColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            tableData.get(row)[4] = event.getNewValue();
            updatePieChart();
        });
        // Инициализация PieChart с начальными данными
        updatePieChart();
    }

    @FXML
    private void importCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        selectedFile = fileChooser.showOpenDialog(importButton.getScene().getWindow());
        if (selectedFile != null) {
            loadDataFromCSV(selectedFile);

        }
    }


    private void loadDataFromCSV(File file) {
        tableData.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                tableData.add(row);
            }
            Platform.runLater(() -> {
                myTableView.setItems(tableData);
                myTableView.setVisible(true);
                updatePieChart();
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading CSV file.");
        }
    }

    @FXML
    private void exportCsv() {
        if (tableData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No data to export.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (file != null) {
            writeDataToCSV(file);
        }
    }


    private void writeDataToCSV(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String[] row : tableData) {
                String line = String.join(",", row);
                bw.write(line);
                bw.newLine();
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Data exported to CSV file.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error exporting CSV file.");
        }
    }

    @FXML
    private void toggleTableVisibility() {
        myTableView.setVisible(showTableRadioButton.isSelected());
    }


    @FXML
    private void editRow() {
        int selectedIndex = myTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            TableColumn<String[], ?> focusedColumn = myTableView.getFocusModel().getFocusedCell().getTableColumn();
            myTableView.requestFocus();
            myTableView.getSelectionModel().select(selectedIndex);
            myTableView.getFocusModel().focus(selectedIndex,focusedColumn);
            myTableView.edit(selectedIndex,focusedColumn);

        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a row to edit.");
        }
    }

    @FXML
    private void insertRow() {
        int selectedIndex = myTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            String[] newRow = new String[myTableView.getColumns().size()];
            tableData.add(selectedIndex+1, newRow);
        } else{
            String[] newRow = new String[myTableView.getColumns().size()];
            tableData.add(newRow);
        }
        updatePieChart();
    }

    @FXML
    private void deleteRow() {
        int selectedIndex = myTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tableData.remove(selectedIndex);
            updatePieChart();
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a row to delete.");
        }
    }

    private void updatePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (String[] row : tableData) {
            try {
                int pages = Integer.parseInt(row[3]);
                PieChart.Data data = new PieChart.Data(row[1], pages);
                pieChartData.add(data);
            } catch (NumberFormatException e) {
                // Обработка неверного формата страниц
            }
        }
        pagesPieChart.setData(pieChartData);


        pieChartData.forEach(data -> {
            StackPane node = new StackPane();
            Text text = new Text(String.valueOf(data.getPieValue()));
            node.getChildren().add(text);
            pagesPieChart.getChildrenUnmodifiable().stream()
                    .filter(n -> n instanceof StackPane && n.getStyleClass().contains("chart-pie-slice"))
                    .forEach(n -> {
                        StackPane sp = (StackPane) n;
                        if (sp.getProperties().get("pie.chart.data").equals(data)) {
                            sp.getChildren().add(text);
                            text.setLayoutX(sp.getLayoutX() - text.getLayoutBounds().getWidth()/2);
                            text.setLayoutY(sp.getLayoutY() + text.getLayoutBounds().getHeight()/4);
                        }
                    });

        });
        pagesPieChart.setLegendSide(Side.LEFT);
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}