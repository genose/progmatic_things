import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChildAppComponent } from './child-app.component';

describe('ChildAppComponent', () => {
  let component: ChildAppComponent;
  let fixture: ComponentFixture<ChildAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChildAppComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChildAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
