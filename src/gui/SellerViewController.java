package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.service.SellerService;

public class SellerViewController implements Initializable, DataChangeListener {

	private SellerService service;

	@FXML
	private Button btNew;
	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;

	public void onBtNewAction(ActionEvent event) {
		String path = "/gui/SellerForm.fxml";
		Stage parentStage = Utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, path, parentStage);
	}

	private ObservableList<Seller> obsList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalAccessError("Service is null");
		}
		List<Seller> lista = service.findAll();
		obsList = FXCollections.observableArrayList(lista);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		/*
		 * try { FXMLLoader loader = new
		 * FXMLLoader(getClass().getResource(absoluteName)); Pane pane = loader.load();
		 * 
		 * SellerFormController controller = loader.getController();
		 * controller.setEntity(obj); controller.updateFormData();
		 * controller.setService(new SellerService());
		 * controller.subscribeDataChangeListener(this);
		 * 
		 * Stage dialogStage = new Stage(); dialogStage.setTitle("Enter Seller data");
		 * dialogStage.setScene(new Scene(pane)); dialogStage.setResizable(false);
		 * dialogStage.initOwner(parentStage);
		 * dialogStage.initModality(Modality.WINDOW_MODAL); dialogStage.showAndWait();
		 * 
		 * } catch (IOException e) { Alerts.showAlert("IO Exception",
		 * "error loading views", e.getMessage(), AlertType.ERROR); }
		 */
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "are you sure about that");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("service is null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}

		}
	}

}