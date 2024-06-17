import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarousselcontentsComponent } from './carousselcontents.component';

describe('CarousselcontentsComponent', () => {
  let component: CarousselcontentsComponent;
  let fixture: ComponentFixture<CarousselcontentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarousselcontentsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CarousselcontentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
