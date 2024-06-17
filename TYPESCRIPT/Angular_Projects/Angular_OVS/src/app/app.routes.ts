import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { RegisterComponent } from './components/register/register.component';
import { AgendaComponent } from './components/agenda/agenda.component';
import { page404Component } from './components/page404/page404.component';

export const routes: Routes = [
  // All your other routes should come first
  {
    path: '',
    component: HomeComponent,
    data: { title: 'Home' },
  },
  {
    path: 'register',
    component: RegisterComponent,
    pathMatch: 'full',
    data: { title: 'Register' },
  },
  {
    path: 'agenda',
    component: AgendaComponent,
    pathMatch: 'full',
    data: { title: 'Agenda' },
  },

  {
    path: '**',
    component: page404Component,
    data: { title: 'Page Not Found' },
  },
];
