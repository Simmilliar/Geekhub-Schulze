import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {UserService} from '../user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  errorMessage: string;
  username: string = '';
  password: string = '';

  constructor(private http: HttpClient, private route: Router, private userService: UserService) { }

  ngOnInit(): void {
  }

  async login(): Promise<void> {
    const formData = new FormData()
    formData.append('username', this.username)
    formData.append('password', this.password)
    this.errorMessage = ''
    try {
      const loginResponse = await this.http.post<any>('/api/login', formData).toPromise()
      if (loginResponse.status === 'OK') {
        this.userService.setUser(await this.http.get<any>('/api/users/me').toPromise())
        await this.route.navigate(['/'])
      }
    } catch (err) {
      if (err.error.message) {
        this.errorMessage = err.error.message
      } else {
        this.errorMessage = 'Login error'
      }
    }
  }
}
