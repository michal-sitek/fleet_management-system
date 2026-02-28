import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RequestStatsResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class StatsService {
  private readonly baseUrl = '/stats/requests';

  constructor(private readonly http: HttpClient) {}

  getStats(): Observable<RequestStatsResponse> {
    return this.http.get<RequestStatsResponse>(this.baseUrl);
  }
}
