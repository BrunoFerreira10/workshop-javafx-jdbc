package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;
import models.exception.ValidationException;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();

	@FXML
	private TextField textFieldId;
	@FXML
	private TextField textFieldName;
	@FXML
	private TextField textFieldEmail;
	@FXML
	private DatePicker datePickerBirthDate;
	@FXML
	private TextField textFieldBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Label lblErrorBaseSalary;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;

	private ObservableList<Department> obsListDepartment;

	public void setEntity(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if (entity == null)
			new IllegalStateException("Entity was null");

		if (service == null)
			new IllegalStateException("Service was null");

		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 70);
		Constraints.setTextFieldMaxLength(textFieldEmail, 60);
		Constraints.setTextFieldDouble(textFieldBaseSalary);
		initializeComboBoxDepartment();

		Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");
	}

	public void updateFormData() {

		if (entity == null)
			throw new IllegalStateException("Entity was null");

		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(entity.getName());
		textFieldEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);

		if (entity.getBirthDate() != null)
			datePickerBirthDate
					.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getDepartment() == null)
			comboBoxDepartment.getSelectionModel().selectFirst();
		else
			comboBoxDepartment.setValue(entity.getDepartment());
	}

	private Seller getFormData() {

		ValidationException validationException = new ValidationException("Validation error.");

		Seller entity = new Seller();
		// Id
		entity.setId(Utils.tryParseToInt(textFieldId.getText()));
		
		// Name
		if (textFieldName.getText() == null || textFieldName.getText().trim().equals(""))
			validationException.addError("name", "Field can't be empty.");
		entity.setName(textFieldName.getText());
		
		// Email
		if (textFieldEmail.getText() == null || textFieldEmail.getText().trim().equals(""))
			validationException.addError("email", "Field can't be empty.");
		entity.setEmail(textFieldEmail.getText());
		
		// BirthDate
		if(datePickerBirthDate.getValue() == null) {
			validationException.addError("birthDate", "Field can't be empty.");
		}	
		else {
			Instant instant = Instant.from(datePickerBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			entity.setBirthDate(Date.from(instant));
		}	
		
		// BaseSalary
		if (textFieldBaseSalary.getText() == null || textFieldBaseSalary.getText().trim().equals(""))
			validationException.addError("baseSalary", "Field can't be empty.");
		entity.setBaseSalary(Utils.tryParseToDouble(textFieldBaseSalary.getText()));
		
		//Department
		entity.setDepartment(comboBoxDepartment.getValue());

		// Validation
		if (validationException.getErrors().size() > 0) {
			throw validationException;
		}

		return entity;
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener dataChangeListener : dataChangeListeners) {
			dataChangeListener.onDataChanged();
		}
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		lblErrorName.setText(fields.contains("name") ? errors.get("name") : "");
		lblErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");
		lblErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
		lblErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");		
		
	}

	public void loadAssociatedObjects() {
		if (departmentService == null)
			throw new IllegalStateException("Department service was null!");

		List<Department> departments = departmentService.findAll();
		obsListDepartment = FXCollections.observableArrayList(departments);
		comboBoxDepartment.setItems(obsListDepartment);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
