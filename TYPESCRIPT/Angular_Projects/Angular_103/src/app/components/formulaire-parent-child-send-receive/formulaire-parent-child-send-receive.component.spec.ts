import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormulaireParentChildSendReceiveComponent as FormulaireParentChildSendReceiveComponent } from './formulaire-parent-child-send-receive.component';

describe('FormulairePrentChildSendReceiveComponent', () => {
  let component: FormulaireParentChildSendReceiveComponent;
  let fixture: ComponentFixture<FormulaireParentChildSendReceiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormulaireParentChildSendReceiveComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(
      FormulaireParentChildSendReceiveComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
