import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-election',
  templateUrl: './election.component.html',
  styleUrls: ['./election.component.scss']
})
export class ElectionComponent implements OnInit {

  election: any

  constructor(public userService: UserService, private http: HttpClient,
              private router: Router, private route: ActivatedRoute) { }

  async ngOnInit(): Promise<void> {
    try {
      const electionShareId = this.route.snapshot.paramMap.get('shareId')
      this.election = await this.http.get<any>('/api/elections/' + electionShareId).toPromise()
    } catch (err) {
      if (err.status === 401) {
        this.userService.resetUser()
        await this.router.navigate(['/login'])
      }
    }
  }
}
