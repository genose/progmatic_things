import { ComponentFixture, TestBed } from '@angular/core/testing';

import { page404Component } from './page404.component';

describe('page404Component', () => {
  let component: page404Component;
  let fixture: ComponentFixture<page404Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [page404Component],
    }).compileComponents();

    fixture = TestBed.createComponent(page404Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
