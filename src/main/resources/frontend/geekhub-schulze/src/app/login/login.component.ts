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

  login(): void {
    const formData = new FormData()
    formData.append('username', this.username)
    formData.append('password', this.password)
    this.errorMessage = ''
    this.http.post<any>('/api/login', formData).subscribe(
      async (res) => {
        if (res.status === 'OK') {
          this.userService.setUser({ name: 'John Doe' })
          await this.route.navigate(['/login'])
        }
      },
      (err) => {
        this.errorMessage = err.error.message
      }
    )
  }
}
