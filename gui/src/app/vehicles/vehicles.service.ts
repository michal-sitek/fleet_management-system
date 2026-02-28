import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse, CreateVehicleRequest, UpdateVehicleRequest, VehicleResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class VehiclesService {
  private readonly baseUrl = '/vehicles';

  constructor(private readonly http: HttpClient) {}

  list(options: {
    q?: string;
    page?: number;
    size?: number;
    sort?: string;
  }): Observable<PageResponse<VehicleResponse>> {
    let params = new HttpParams();

    if (options.q) params = params.set('q', options.q);
    params = params.set('page', String(options.page ?? 0));
    params = params.set('size', String(options.size ?? 20));
    if (options.sort) params = params.set('sort', options.sort);

    return this.http.get<PageResponse<VehicleResponse>>(this.baseUrl, { params });
  }

  getById(id: string): Observable<VehicleResponse> {
    return this.http.get<VehicleResponse>(`${this.baseUrl}/${id}`);
  }

  create(req: CreateVehicleRequest): Observable<VehicleResponse> {
    return this.http.post<VehicleResponse>(this.baseUrl, req);
  }

  update(id: string, req: UpdateVehicleRequest): Observable<VehicleResponse> {
    return this.http.put<VehicleResponse>(`${this.baseUrl}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
