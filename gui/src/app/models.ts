export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type VehicleStatus = 'ACTIVE' | 'IN_SERVICE' | 'SOLD';

export interface VehicleResponse {
  id: string;
  plateNumber: string;
  vin: string;
  brand: string;
  model: string;
  year: number;
  status: VehicleStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateVehicleRequest {
  plateNumber: string;
  vin: string;
  brand: string;
  model: string;
  year: number;
  status: VehicleStatus;
}

export interface UpdateVehicleRequest extends CreateVehicleRequest {}

export interface RequestStatsResponse {
  totalRequests: number;
  perEndpoint: Record<string, number>;
  perStatus: Record<string, number>;
}
