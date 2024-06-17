import { Component, input } from '@angular/core'; // import de Component
import { CarousselimagesComponent } from '../carousselimages/carousselimages.component';
// direction de la carte
enum flipDirectionEnum {
  horizontalTop = ' rotateX(-180deg)', // rotateX(-180deg)
  horizontalBottom = ' rotateX(180deg)', // rotateX(180deg)
  verticalLeft = ' rotateY(-180deg)', // rotateY(-180deg)
  verticalRight = ' rotateY(180deg)', // rotateY(180deg)
}

@Component({
  selector: 'app-flipcard',
  standalone: true,
  imports: [CarousselimagesComponent],
  templateUrl: './flipcard.component.html',
  styleUrl: './flipcard.component.css',
})
export class FlipcardComponent {
  hauteur = 200; // hauteur de la carte
  largeur = 300; // largeur de la carte

  titre = 'Titre de la carte'; // titre de la carte
  contenuLong = 'Contenu de la carte'; // contenu de la carte
  descriptionCourte = 'Description de la carte'; // description de la carte

  date: string = '12/12/2021'; // date de la carte
  auteur: string = 'Auteur de la carte'; // auteur de la carte

  image = 'https://via.placeholder.com/150'; // image de la carte

  // liste d'images de la carte
  imagesList = [
    'https://via.placeholder.com/150',
    'https://via.placeholder.com/150',
    'https://via.placeholder.com/150',
  ];

  flip = false; // état de la carte
  flipDirection: flipDirectionEnum = flipDirectionEnum.verticalLeft; // direction de la carte
  images: string[] = []; // images de la carte
}
