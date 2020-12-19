import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  errorMessage: string
  username: string = ''
  password: string = ''

  constructor(private http: HttpClient, private route: Router) { }

  ngOnInit(): void {
  }

  registration(): void {
    this.errorMessage = ''
    this.http.post<any>('/api/registration', {
      login: this.username,
      password: this.password
    }).subscribe(
      (res) => {
        if (res.status === 'OK') {
          this.route.navigate(['/login'])
        }
      },
      (err) => {
        if (err.status === 409) {
          this.errorMessage = err.error.message
        } else if (err.status === 400) {
          this.errorMessage = err.error.errors.map(error => {
            return error.field + ': ' + error.defaultMessage
          }).join('\n')
        }
      }
    )
  }
}
