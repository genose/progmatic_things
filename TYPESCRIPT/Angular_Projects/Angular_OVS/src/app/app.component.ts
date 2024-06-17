import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FlipcardComponent } from './components/flipcard/flipcard.component';
import { CarousselcontentsComponent } from './components/carousselcontents/carousselcontents.component';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  imports: [
    RouterOutlet,
    FlipcardComponent,
    CarousselcontentsComponent,
    CommonModule,
    HeaderComponent,
  ],
})
export class AppComponent {
  title = 'Angular_OVS';
}
