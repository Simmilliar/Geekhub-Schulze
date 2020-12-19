import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  elections: any[] = []
  participations: any[] = []

  constructor(public userService: UserService, private http: HttpClient, private route: Router) { }

  async ngOnInit(): Promise<void> {
    try {
      const elections = await this.http.get<any>('/api/elections').toPromise()
      this.elections = elections.items
      const participations = await this.http.get<any>('/api/participations').toPromise()
      this.participations = participations.items
    } catch (err) {
      if (err.status === 401) {
        this.userService.resetUser()
        await this.route.navigate(['/login'])
      }
    }
  }
}
