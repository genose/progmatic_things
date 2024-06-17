import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CarousselcontentsComponent } from '../carousselcontents/carousselcontents.component';
import { FlipcardComponent } from '../flipcard/flipcard.component';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  imports: [CarousselcontentsComponent, FlipcardComponent],
})
export class HomeComponent {
  constructor() {
    console.log('HomeComponent');
  }
}
