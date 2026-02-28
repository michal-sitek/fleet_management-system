import { Routes } from '@angular/router';
import { VehiclesListComponent } from './vehicles/vehicles-list.component';
import { VehicleFormComponent } from './vehicles/vehicle-form.component';
import { StatsComponent } from './stats/stats.component';

export const routes: Routes = [
  { path: '', redirectTo: 'vehicles', pathMatch: 'full' },

  { path: 'vehicles', component: VehiclesListComponent },
  { path: 'vehicles/new', component: VehicleFormComponent },
  { path: 'vehicles/:id/edit', component: VehicleFormComponent },

  { path: 'stats', component: StatsComponent },

  { path: '**', redirectTo: 'vehicles' },
];
