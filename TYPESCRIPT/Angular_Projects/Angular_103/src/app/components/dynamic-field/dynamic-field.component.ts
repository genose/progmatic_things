import { Component } from '@angular/core';

@Component({
  selector: 'app-dynamic-field',
  standalone: true,
  imports: [],
  templateUrl: './dynamic-field.component.html',
  styleUrl: './dynamic-field.component.css',
})
export class DynamicFieldComponent {
  @Input() field: {};
  @Input() formName: string;
}
