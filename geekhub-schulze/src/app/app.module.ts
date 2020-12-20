import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import {Router, RouterModule, Routes} from '@angular/router';
import { HomeComponent } from './home/home.component';
import { RegistrationComponent } from './registration/registration.component';
import { ElectionComponent } from './election/election.component';
import { NewElectionComponent } from './new-election/new-election.component';
import { VotingComponent } from './voting/voting.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {AuthInterceptor} from "./auth.interceptor";
import {UserService} from "./user.service";

const routes: Routes = [{
  path: '', pathMatch: 'full', component: HomeComponent
}, {
  path: 'login', component: LoginComponent
}, {
  path: 'registration', component: RegistrationComponent
}, {
  path: 'elections/new', component: NewElectionComponent
}, {
  path: 'elections/:shareId', component: ElectionComponent
}, {
  path: 'elections/:shareId/voting', component: VotingComponent
}, {
  path: '**', redirectTo: ''
}]

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    RegistrationComponent,
    ElectionComponent,
    NewElectionComponent,
    VotingComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useFactory: function (router: Router, userService: UserService) {
      return new AuthInterceptor(router, userService)
    },
    multi: true,
    deps: [Router, UserService]
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
