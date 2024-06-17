import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ChildAppComponent } from '../child-app/child-app.component';

@Component({
  selector: 'app-formulaire-parent-child-send-receive',
  standalone: true,
  imports: [ChildAppComponent],
  templateUrl: './formulaire-parent-child-send-receive.component.html',
  styleUrl: './formulaire-parent-child-send-receive.component.css',
})
export class FormulaireParentChildSendReceiveComponent {
  localFormParentName: string = 'Parent Name';
  parentLocalValue: string = 'Parent children content !!!';

  @Output() parentSendToChildEmitter: EventEmitter<any> = new EventEmitter();
  @Input() parentReceiveFromChildEmitter: EventEmitter<any> =
    new EventEmitter<any>();

  parentReceiveFromChildOutput($event: any) {
    console.log(
      'FormulaireParentChildSendReceiveComponent parentReceiveFromChildOutput'
    );
    console.log($event);
  }

  parentSentToChildInput($event: any) {
    console.log(
      'FormulaireParentChildSendReceiveComponent parentsentToChildInput'
    );
    console.log($event);
  }
}
