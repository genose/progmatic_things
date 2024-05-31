import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { HomeComponent } from './components/home-component/home-component.component';
import { BlockComponent } from './components/block/block.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { ArticleComponent } from './components/article/article.component';
import { FormulaireComponent } from './components/formulaire/formulaire.component';
import { FormulaireRegisterComponent } from './components/formulaire-register/formulaire-register.component';
import { FormulaireParentChildSendReceiveComponent } from './components/formulaire-parent-child-send-receive/formulaire-parent-child-send-receive.component';

export const routes: Routes = [
  // All your other routes should come first
  {
    path: 'home',
    component: HomeComponent,
    data: { title: 'Home' },
  },

  // Block Component
  { path: 'block', pathMatch: 'full', component: BlockComponent },
  { path: 'block/:id', pathMatch: 'full', component: BlockComponent },
  { path: 'block/:id/:name', pathMatch: 'full', component: BlockComponent },
  {
    path: 'block/:id/:name/:age',
    pathMatch: 'full',
    component: BlockComponent,
  },
  // Article Component
  { path: 'article', pathMatch: 'full', component: ArticleComponent },
  { path: 'article/:id', pathMatch: 'full', component: ArticleComponent },
  { path: 'article/:id/:name', pathMatch: 'full', component: ArticleComponent },
  {
    path: 'article/:id/:name/:age',
    pathMatch: 'full',
    component: ArticleComponent,
  },
  { path: 'formulaire', pathMatch: 'full', component: FormulaireComponent },
  {
    path: 'child-parent-form',
    pathMatch: 'full',
    component: FormulaireParentChildSendReceiveComponent,
  },
  {
    path: 'register',
    pathMatch: 'full',
    component: FormulaireRegisterComponent,
  },
  // 404 Page
  { path: '404', component: PageNotFoundComponent },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
