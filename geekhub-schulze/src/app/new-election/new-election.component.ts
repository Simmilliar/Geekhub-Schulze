import { Component, OnInit } from '@angular/core';
import {UserService} from "../user.service";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from "@angular/router";
import {NewElectionForm} from "./new-election.form";

@Component({
  selector: 'app-new-election',
  templateUrl: './new-election.component.html',
  styleUrls: ['./new-election.component.scss']
})
export class NewElectionComponent implements OnInit {

  election: NewElectionForm
  errorMessage: string

  constructor(public userService: UserService, private http: HttpClient,
              private router: Router, private route: ActivatedRoute) {
    this.election = {
      title: '',
      description: '',
      candidates: []
    }
  }

  addCandidate(): void {
    this.election.candidates.push({ name: '', description: '' })
  }

  removeCandidate(i: number): void {
    this.election.candidates.splice(i, 1)
  }

  async save(): Promise<void> {
    this.errorMessage = ''
    try {
      const newElection = await this.http.post<any>('/api/elections/', this.election).toPromise()
      await this.router.navigate(['/elections/' + newElection.shareId])
    } catch (err) {
      if (err.status === 401) {
        this.userService.resetUser()
        await this.router.navigate(['/login'])
      } else if (err.status === 400) {
        this.errorMessage = err.error.errors.map(error => {
          return error.field + ': ' + error.defaultMessage
        }).join('\n')
      } else if (err.status === 409) {
        this.errorMessage = err.error.message
      }
    }
  }

  ngOnInit(): void {}
}
