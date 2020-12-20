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

  async registration(): Promise<void> {
    this.errorMessage = ''
    try {
      const res = await this.http.post<any>('/api/registration', {
        login: this.username,
        password: this.password
      }).toPromise()
      if (res.status === 'OK') {
        await this.route.navigate(['/login'])
      }
    } catch (err) {
      if (err.status === 409) {
        this.errorMessage = err.error?.message
      } else if (err.status === 400) {
        this.errorMessage = err.error?.errors?.map(error => {
          return error.field + ': ' + error.defaultMessage
        })?.join('\n') || 'Unknown error'
      } else {
        this.errorMessage = 'Unknown error'
      }
    }
  }
}
