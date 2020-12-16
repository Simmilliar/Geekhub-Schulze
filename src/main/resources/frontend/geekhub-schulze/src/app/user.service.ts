import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor() {}

  getUser() {
    return JSON.parse(localStorage.getItem('user'))
  }

  setUser(user: { name: string }) {
    localStorage.setItem('user', JSON.stringify(user))
  }

  resetUser() {
    localStorage.removeItem('user')
  }
}
