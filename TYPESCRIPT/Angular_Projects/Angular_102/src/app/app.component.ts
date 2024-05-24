import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BlockComponent } from './block/block.component';
import {CommonModule} from '@angular/common'; // Import NgFor
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, BlockComponent, CommonModule ], // Add NgFor to imports
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Angular_102';
}
