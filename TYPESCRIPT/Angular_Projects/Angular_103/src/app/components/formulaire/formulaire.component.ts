import { Component, OnInit, VERSION } from '@angular/core';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';

@Component({
  selector: 'app-formulaire',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './formulaire.component.html',
  styleUrl: './formulaire.component.css',
})
export class FormulaireComponent implements OnInit {
  registerForm: FormGroup;

  ngOnInit() {
    console.log('FormulaireComponent initialized');
    console.log('Angular version: ', VERSION.full);
  }

  constructor() {
    this.registerForm = new FormGroup({
      name: new FormControl(''),
      lastName: new FormControl(''),
      age: new FormControl(''),
    });
  }

  onSubmit(): void {
    // form submitted
    console.log('Formulaire envoyé !');
  }
}
