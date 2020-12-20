import { Injectable } from '@angular/core';
import {UserModel} from "./_models/user.model";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor() {}

  getUser(): UserModel {
    return JSON.parse(localStorage.getItem('user'))
  }

  setUser(user: UserModel): void {
    localStorage.setItem('user', JSON.stringify(user))
  }

  resetUser(): void {
    localStorage.removeItem('user')
  }
}
