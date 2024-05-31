import { NgFor } from '@angular/common';
import { Component, Input, OnInit, VERSION } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';

@Component({
  selector: 'app-formulaire-register',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, NgFor],
  templateUrl: './formulaire-register.component.html',
  styleUrl: './formulaire-register.component.css',
})
export class FormulaireRegisterComponent implements OnInit {
  @Input()
  formFieldValue!: string;

  registerFormGroup!: FormGroup;
  fields: Array<string> = [];

  simpleModel = {
    name: '',
    lastName: '',
    address: '',
    age: '',
  };

  constructor() {
    console.log('FormulaireRegisterComponent constructor');
  }
  onNgInit() {
    console.log('FormulaireRegisterComponent initialized');
    this.buildForm();
  }

  getFormControlsFields() {
    const formGroupFields: { [key: string]: FormControl } = {}; // Explicitly define the type for formGroupFields
    for (const field of Object.keys(this.simpleModel)) {
      // Remove the type annotation in the for...of loop
      formGroupFields[field] = new FormControl('');
      this.fields.push(field);
    }
    return formGroupFields;
  }

  buildForm() {
    const formGroupFields = this.getFormControlsFields();
    this.registerFormGroup = new FormGroup(formGroupFields);
  }

  ngOnInit() {
    console.log('FormulaireRegisterComponent initialized');
    console.log('Angular version: ', VERSION.full);
    this.buildForm();
  }
  onsubmit() {
    console.log('FormulaireRegisterComponent submitted');
    console.log(this.registerFormGroup?.value);
  }
}
