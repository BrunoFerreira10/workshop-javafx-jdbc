package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.services.SellerService;
import models.exception.ValidationException;

public class SellerFormController implements Initializable {
	
	private Seller entity;
	private SellerService service;
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
	
	public void setEntity(Seller entity) {
		this.entity = entity;
	}
	
	public void setService(SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null)
			new IllegalStateException("Entity was null");
		
		if(service == null)
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
		
		Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");
	}
	
	public void updateFormData() {
		
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		
		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(entity.getName());
		textFieldEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		
		if(entity.getBirthDate() != null)
			datePickerBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(),ZoneId.systemDefault()));
		textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
	}
	
	private Seller getFormData() {
		
		ValidationException validationException = new ValidationException("Validation error.");
		
		Seller entity = new Seller();
		entity.setId(Utils.tryParseToInt(textFieldId.getText()));
		
		if(textFieldName.getText() == null || textFieldName.getText().trim().equals(""))
			validationException.addError("name", "Field can't be empty.");
		entity.setName(textFieldName.getText());
		
		if(validationException.getErrors().size() > 0) {
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
		
		if(fields.contains("name"))
			lblErrorName.setText(errors.get("name"));
	}
	
	
	
}
