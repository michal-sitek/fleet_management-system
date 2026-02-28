import { HttpInterceptorFn } from '@angular/common/http';

export const basicAuthInterceptor: HttpInterceptorFn = (req, next) => {
  const username = localStorage.getItem('auth.username') ?? 'admin';
  const password = localStorage.getItem('auth.password') ?? 'admin';

  const header = 'Basic ' + btoa(username + ':' + password);

  return next(req.clone({ setHeaders: { Authorization: header } }));
};
