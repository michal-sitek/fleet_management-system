import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatsService } from './stats.service';
import { RequestStatsResponse } from '../models';

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.scss',
})
export class StatsComponent {
  loading = false;
  error: string | null = null;
  data: RequestStatsResponse | null = null;

  constructor(private readonly statsService: StatsService) {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;

    this.statsService.getStats().subscribe({
      next: (res) => {
        this.data = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load stats';
        this.loading = false;
      },
    });
  }

  keys(obj: Record<string, number> | undefined): string[] {
    if (!obj) return [];
    return Object.keys(obj).sort();
  }
}
