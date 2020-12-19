import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-voting',
  templateUrl: './voting.component.html',
  styleUrls: ['./voting.component.scss']
})
export class VotingComponent implements OnInit {

  electionShareId: string
  election: any
  candidates: any
  pairsAvailable: boolean = false

  constructor(public userService: UserService, private http: HttpClient,
              private router: Router, private route: ActivatedRoute) { }

  async ngOnInit(): Promise<void> {
    try {
      this.electionShareId = this.route.snapshot.paramMap.get('shareId')
      this.election = await this.http.get<any>('/api/elections/' + this.electionShareId).toPromise()
      this.candidates = await this.http.get<any>('/api/elections/' + this.electionShareId + '/vote').toPromise()
    } catch (err) {
      if (err.status === 401) {
        this.userService.resetUser()
        await this.router.navigate(['/login'])
      }
    }
  }

  async vote(leftBetterThanRight: boolean): Promise<void> {
    try {
      await this.http.post<any>('/api/elections/' + this.electionShareId + '/vote', {
        voteId: this.candidates.voteId,
        leftBetterThanRight
      }).toPromise()
      const newPair = await this.http.get<any>('/api/elections/' + this.electionShareId + '/vote').toPromise()
      console.log(newPair)
      this.candidates = newPair
    } catch (err) {
      if (err.status === 401) {
        this.userService.resetUser()
        await this.router.navigate(['/login'])
      }
    }
  }
}
