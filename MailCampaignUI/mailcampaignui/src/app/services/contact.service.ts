import { Injectable } from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Contact, ContactListResponse} from "../contact/contact";

@Injectable({
  providedIn: 'root'
})
export class ContactService {

  constructor(private http: HttpClient) {}

  getContacts(pageNumber : number, pageSize: number) : Observable<ContactListResponse> {
    let params = new HttpParams().set('page', pageNumber).set('pageSize', pageSize);
    return this.http.get<ContactListResponse>(environment.apiBaseUrl + '/contact', {params});
  }

  deleteContact(contact : Contact) {
    return this.http.delete(environment.apiBaseUrl + '/contact/' + contact.pk);
  }

  updateContact(contact : Contact) {
    return this.http.put(environment.apiBaseUrl + '/contact/' + contact.pk, contact);
  }

  addContact(contact : Contact) {
    return this.http.post(environment.apiBaseUrl + '/contact', contact);
  }
}
