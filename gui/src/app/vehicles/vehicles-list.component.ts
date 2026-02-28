import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VehiclesService } from './vehicles.service';
import { PageResponse, VehicleResponse } from '../models';

@Component({
  selector: 'app-vehicles-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './vehicles-list.component.html',
  styleUrl: './vehicles-list.component.scss',
})
export class VehiclesListComponent {
  q = '';
  page = 0;
  size = 10;
  sort = 'createdAt,desc';

  loading = false;
  error: string | null = null;

  data: PageResponse<VehicleResponse> | null = null;

  constructor(private readonly vehiclesService: VehiclesService) {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;

    this.vehiclesService
      .list({ q: this.q || undefined, page: this.page, size: this.size, sort: this.sort })
      .subscribe({
        next: (res) => {
          this.data = res;
          this.loading = false;
        },
        error: (err) => {
          this.error = err?.error?.message ?? 'Request failed';
          this.loading = false;
        },
      });
  }

  search(): void {
    this.page = 0;
    this.load();
  }

  nextPage(): void {
    if (!this.data) return;
    if (this.page + 1 >= this.data.totalPages) return;
    this.page++;
    this.load();
  }

  prevPage(): void {
    if (this.page <= 0) return;
    this.page--;
    this.load();
  }

  remove(id: string): void {
    if (!confirm('Delete vehicle?')) return;

    this.vehiclesService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => (this.error = err?.error?.message ?? 'Delete failed'),
    });
  }
}
