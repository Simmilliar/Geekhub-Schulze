import { Component, OnInit } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserService} from "../user.service";
import {ElectionModel} from "../_models/election.model";
import {Page} from "../_models/page.model";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  elections: ElectionModel[] = []
  participations: ElectionModel[] = []

  constructor(public userService: UserService, private http: HttpClient) { }

  async ngOnInit(): Promise<void> {
    try {
      const elections = await this.http.get<Page<ElectionModel>>('/api/elections').toPromise()
      this.elections = elections.items
      const participations = await this.http.get<Page<ElectionModel>>('/api/participations').toPromise()
      this.participations = participations.items
    } catch (err) {}
  }
}
