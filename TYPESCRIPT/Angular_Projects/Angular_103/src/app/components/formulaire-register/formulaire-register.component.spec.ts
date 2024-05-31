import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormulaireRegisterComponent } from './formulaire-register.component';
import { VERSION } from '@angular/core';

describe('FormulaireRegisterComponent', () => {
  let component: FormulaireRegisterComponent;
  let fixture: ComponentFixture<FormulaireRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormulaireRegisterComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormulaireRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the component', () => {
    // Arrange
    spyOn(console, 'log'); // Spy on console.log to check if it's called

    // Act
    component.ngOnInit();

    // Assert
    expect(component.registerForm).toBeDefined(); // Check if registerForm is defined
    expect(console.log).toHaveBeenCalledWith(
      'FormulaireRegisterComponent initialized'
    ); // Check if console.log is called with the correct message
    expect(console.log).toHaveBeenCalledWith('Angular version: ', VERSION.full); // Check if console.log is called with the correct message
  });
});
// Path: src/app/components/formulaire-register/formulaire-register.component.spec.ts
