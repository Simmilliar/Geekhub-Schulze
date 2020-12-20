import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {ElectionModel} from "../_models/election.model";

@Component({
  selector: 'app-election',
  templateUrl: './election.component.html',
  styleUrls: ['./election.component.scss']
})
export class ElectionComponent implements OnInit {

  electionShareId: string
  election: ElectionModel

  constructor(public userService: UserService, private http: HttpClient,
              private router: Router, private route: ActivatedRoute) { }

  async ngOnInit(): Promise<void> {
    try {
      this.electionShareId = this.route.snapshot.paramMap.get('shareId')
      this.election = await this.http.get<ElectionModel>('/api/elections/' + this.electionShareId).toPromise()
    } catch (err) {}
  }

  async close(): Promise<void> {
    try {
      await this.http.delete<any>('/api/elections/' + this.electionShareId).toPromise()
      await this.router.navigate(['/elections'])
    } catch (err) {}
  }
}
