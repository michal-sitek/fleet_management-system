import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { VehiclesService } from './vehicles.service';
import { CreateVehicleRequest, UpdateVehicleRequest, VehicleStatus } from '../models';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './vehicle-form.component.html',
  styleUrl: './vehicle-form.component.scss',
})
export class VehicleFormComponent {
  id: string | null = null;
  loading = false;
  error: string | null = null;

  statuses: VehicleStatus[] = ['ACTIVE', 'IN_SERVICE', 'SOLD'];

  model: CreateVehicleRequest = {
    plateNumber: '',
    vin: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    status: 'ACTIVE',
  };

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly vehiclesService: VehiclesService
  ) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.id = id;
      this.loadForEdit(id);
    }
  }

  private loadForEdit(id: string): void {
    this.loading = true;
    this.vehiclesService.getById(id).subscribe({
      next: (v) => {
        this.model = {
          plateNumber: v.plateNumber,
          vin: v.vin,
          brand: v.brand,
          model: v.model,
          year: v.year,
          status: v.status,
        };
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load vehicle';
        this.loading = false;
      },
    });
  }

  save(): void {
    this.error = null;
    this.loading = true;

    const req: UpdateVehicleRequest = { ...this.model };

    const call$ = this.id
      ? this.vehiclesService.update(this.id, req)
      : this.vehiclesService.create(req);

    call$.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/vehicles');
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Save failed';
        this.loading = false;
      },
    });
  }
}
