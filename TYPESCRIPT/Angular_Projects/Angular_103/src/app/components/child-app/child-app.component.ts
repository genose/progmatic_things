import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'child-app',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './child-app.component.html',
  styleUrl: './child-app.component.css',
})
/** ************************************************************ */
export class ChildAppComponent {
  // ***************************************************************
  @Input() childInputEmitter: EventEmitter<any> = new EventEmitter<any>();
  @Output() childOutputEmitter: EventEmitter<any> = new EventEmitter<any>();

  // ***************************************************************
  childInputValue: string = 'Child local Value';
  childFormName: string = 'Child Component Name';
  // ***************************************************************
  childFormControl = new FormGroup({
    childFieldDynamic: new FormControl('Child Dynamic Value'),
    childFieldStatic: new FormControl('Child Static Value'),
  });
  /** ************************************************************ */
  sendFormFormChild() {
    throw new Error('Method not implemented.');
  }
  sendDataFormChild() {
    throw new Error('Method not implemented.');
  }

  resetDataFormChild() {
    console.log('ChildAppComponent resetDataFormChild');
  }
  /** ************************************************************ */
  constructor() {
    console.log('ChildAppComponent constructor');
  }
  /** ************************************************************ */
  sendSubmitFormChild($event: any) {
    console.log('ChildAppComponent sendSubmitFormChild');
    console.log($event);
    this.childOutputEmitter.emit(
      this.childFormControl || 'No data to send' // this.childFormControl?.value.childFieldDynamic || "No data to send")
    );
  }
  /** ************************************************************ */
  sendDynamicFieldChild($event: any) {
    console.log('ChildAppComponent sendDynamicFieldChild');
    console.log($event);
    console.log(this.childFormControl.value.childFieldDynamic);
    this.childOutputEmitter.emit(
      this.childFormControl?.value.childFieldDynamic || 'No data to send'
    );
  }
  /** ************************************************************ */
  sendStaticFieldChild($event: any) {
    console.log('ChildAppComponent sendStaticFieldChild');
    console.log($event);
    console.log(this.childFormControl.value.childFieldStatic);
    console.log(
      this.childFormControl.value.childFieldStatic || 'No data to send'
    );
  }
}

// Path: src/app/components/child-app/child-app.component.html
