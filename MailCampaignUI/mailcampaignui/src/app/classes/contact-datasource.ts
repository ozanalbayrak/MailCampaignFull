import { DataSource } from '@angular/cdk/table';
import { CollectionViewer } from '@angular/cdk/collections';
import { Observable, BehaviorSubject, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import {ContactService} from "../services/contact.service";
import {Contact, ContactListResponse} from "../contact/contact";

export class ContactDatasource implements DataSource<Contact>{

  private contactSubject = new BehaviorSubject<Contact[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private countSubject = new BehaviorSubject<number>(0);
  public counter$ = this.countSubject.asObservable();

  constructor(private contactService: ContactService) { }

  connect(collectionViewer: CollectionViewer): Observable<Contact[]> {
    return this.contactSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.contactSubject.complete();
    this.loadingSubject.complete();
    this.countSubject.complete();
  }

  getContacts(pageNumber = 0, pageSize = 5) {
    this.loadingSubject.next(true);
    // @ts-ignore
    this.contactService.getContacts(pageNumber, pageSize)
      .pipe(
        catchError(() => of([])),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe((result: ContactListResponse | any) => {
          this.contactSubject.next(result.data);
          this.countSubject.next(result.totalElements);
        }
      );
  }
}
