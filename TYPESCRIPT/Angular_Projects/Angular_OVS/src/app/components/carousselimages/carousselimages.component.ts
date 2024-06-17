import { Component } from '@angular/core';

@Component({
  selector: 'app-carousselimages',
  standalone: true,
  imports: [],
  templateUrl: './carousselimages.component.html',
  styleUrl: './carousselimages.component.css',
})
export class CarousselimagesComponent {
  images = [700, 800, 807].map((n) => `https://picsum.photos/id/${n}/900/500`);

  /*constructor(config: NgbCarouselConfig) {
    // customize default values of carousels used by this component tree
    config.interval = 2000;
    config.keyboard = true;
    config.pauseOnHover = true;
  }*/
}
