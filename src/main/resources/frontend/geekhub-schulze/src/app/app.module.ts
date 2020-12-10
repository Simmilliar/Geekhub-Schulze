import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import {RouterModule, Routes} from '@angular/router';
import { HomeComponent } from './home/home.component';
import { RegistrationComponent } from './registration/registration.component';
import { ElectionComponent } from './election/election.component';
import { NewElectionComponent } from './new-election/new-election.component';

const routes: Routes = [{
  path: '', pathMatch: 'full', redirectTo: 'home'
}, {
  path: 'home', component: HomeComponent
}, {
  path: 'login', component: LoginComponent
}, {
  path: 'registration', component: RegistrationComponent
}, {
  path: 'election', component: ElectionComponent
}, {
  path: 'election/new', component: NewElectionComponent
}]

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    RegistrationComponent,
    ElectionComponent,
    NewElectionComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
