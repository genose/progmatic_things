import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    HeaderComponent,
    FooterComponent,
    FormsModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'Angular_103';

  constructor() {
    console.log(
      'AppComponent constructor ' + Component.name + ' : ' + this.title
    );
  }

  ngOnInit() {
    console.log('AppComponent ngOnInit');
  }

  ngOnDestroy() {
    console.log('AppComponent ngOnDestroy' + this.title);
  }

  ngOnChanges() {
    console.log('AppComponent ngOnChanges' + this.title);
  }

  ngDoCheck() {
    console.log('AppComponent ngDoCheck');
  }

  ngAfterContentInit() {
    console.log('AppComponent ngAfterContentInit');
  }

  ngAfterContentChecked() {
    console.log('AppComponent ngAfterContentChecked');
  }

  ngAfterViewInit() {
    console.log('AppComponent ngAfterViewInit' + this.title);
  }

  ngAfterViewChecked() {
    console.log('AppComponent ngAfterViewChecked');
  }
}
