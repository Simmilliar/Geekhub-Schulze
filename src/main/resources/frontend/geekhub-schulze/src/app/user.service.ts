import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private user: { name: string }

  constructor() {
    this.setUser({ name: '' })
  }

  getUser() {
    return this.user
  }

  setUser(user: { name: string }) {
    this.user = user
  }

  resetUser() {
    this.user = { name: '' }
  }
}
