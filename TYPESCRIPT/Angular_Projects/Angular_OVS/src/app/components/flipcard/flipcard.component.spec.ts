import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlipcardComponent } from './flipcard.component';

describe('FlipcardComponent', () => {
  let component: FlipcardComponent;
  let fixture: ComponentFixture<FlipcardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlipcardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FlipcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
