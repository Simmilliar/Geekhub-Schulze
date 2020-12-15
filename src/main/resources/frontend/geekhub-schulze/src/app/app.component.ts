import { Component } from '@angular/core';
import {UserService} from './user.service';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  title = 'VOTE';

  constructor(public userService: UserService, private http: HttpClient, private route: Router) {
  }

  logout() {
    this.http.post<any>('/api/logout', {}).subscribe(
      async () => {
        this.userService.resetUser()
        await this.route.navigate(['/login'])
      },
      (err) => {}
    )
  }
}
