import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  username = localStorage.getItem('auth.username') ?? 'admin';
  password = localStorage.getItem('auth.password') ?? 'admin';

  saveAuth(): void {
    localStorage.setItem('auth.username', this.username);
    localStorage.setItem('auth.password', this.password);
    alert('Dane Basic Auth zapisane.');
  }
}
