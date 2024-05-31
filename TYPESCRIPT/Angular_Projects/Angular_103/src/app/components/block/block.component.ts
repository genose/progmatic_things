import { Component } from '@angular/core';

@Component({
  selector: 'app-block',
  standalone: true,
  imports: [],
  templateUrl: './block.component.html',
  styleUrl: './block.component.css',
})
export class BlockComponent {
  title: string = 'Block Component';
  blockhaussHREF: string =
    'https://commons.wikimedia.org/w/index.php?curid=128001603';
  blockhaussImageSrc: string =
    'https://upload.wikimedia.org/wikipedia/commons/thumb/7/79/Grand_blockhaus%2C_Batz-sur-Mer-4866.jpg/2560px-Grand_blockhaus%2C_Batz-sur-Mer-4866.jpg';
  blockhaussImageTextAlt: string = 'Blockhaus de Batz-sur-Mer';
  constructor() {
    console.log('BlockComponent constructor');
  }
  ngOnInit() {
    console.log('BlockComponent ngOnInit');
  }
  ngOnDestroy() {
    console.log('BlockComponent ngOnDestroy');
  }
  ngOnChanges() {
    console.log('BlockComponent ngOnChanges');
  }
  ngDoCheck() {
    console.log('BlockComponent ngDoCheck');
  }
  ngAfterContentInit() {
    console.log('BlockComponent ngAfterContentInit');
  }
  ngAfterContentChecked() {
    console.log('BlockComponent ngAfterContentChecked');
  }
  ngAfterViewInit() {
    console.log('BlockComponent ngAfterViewInit');
  }
  ngAfterViewChecked() {
    console.log('BlockComponent ngAfterViewChecked');
  }
}
