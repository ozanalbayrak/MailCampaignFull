import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Contact} from "../contact/contact";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class EmailService {

  constructor(private http: HttpClient) { }

  sendEmail(contacts : Contact[], body: string) {
    return this.http.put(environment.apiBaseUrl + '/contact/email', {contacts: contacts, mailBody: body});
  }
}
