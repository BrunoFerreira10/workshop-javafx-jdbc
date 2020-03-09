package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	private Department entity;

	@FXML
	private TextField textFieldId;
	@FXML
	private TextField textFieldName;
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	
	public void setEntity(Department entity) {
		this.entity = entity;
	}

	@FXML
	public void onBtnSaveAction() {
		
	}
	
	@FXML
	public void onBtnCancelAction() {
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();		
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldMaxLength(textFieldName, 30);
	}
	
	public void updateFormData() {
		
		if(entity == null)
			throw new IllegalStateException("Entity was null");
		
		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(String.valueOf(entity.getName()));
	}

}
