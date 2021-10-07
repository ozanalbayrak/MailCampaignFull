import {AfterViewInit, Component, Inject, OnInit, ViewChild} from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
import {MatPaginator} from "@angular/material/paginator";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ContactService} from "../../services/contact.service";
import {Contact} from "../../contact/contact";
import {ContactDatasource} from "../../classes/contact-datasource";
import {tap} from "rxjs/operators";
import {FileUploadComponent} from "../file-upload/file-upload.component";
import {EmailService} from "../../services/email.service";

@Component({
  selector: 'app-contact-table',
  templateUrl: './contact-table.component.html',
  styleUrls: ['./contact-table.component.css']
})
export class ContactTableComponent implements AfterViewInit, OnInit {

  // @ts-ignore
  @ViewChild(MatPaginator) paginator: MatPaginator;

  selection = new SelectionModel<Contact>(true, []);
  displayedColumns: string[] = ['select', 'fullName', 'email', 'emailSent', 'howLong', 'action'];
  dataSource: ContactDatasource;
  body: string;

  constructor(public dialog: MatDialog, private contactService: ContactService, private emailService: EmailService) {
    this.dataSource = new ContactDatasource(this.contactService);
    this.body = "";
  }

  addContact() {
    const dialogRef = this.dialog.open(ContactModalComponent, {
      width: '250px',
      data: {actionAdd: true}
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getContacts();
    });
  }

  editContact(element: Contact) {
      const temp: Contact = { pk: element.pk,
                            fullName: element.fullName,
                            email: element.email,
                            emailSent: false,
                            howLong: "",
                            isEmailDone: false};
      const dialogRef = this.dialog.open(ContactModalComponent, {
        width: '250px',
        data: {contact: temp, actionAdd: false}
      });
      dialogRef.afterClosed().subscribe(result => {
        this.getContacts();
      });
  }

  deleteContact(element: Contact) {
    this.contactService.deleteContact(element).subscribe(result => {
      this.showAlert("Successful");
    }, error => {
      this.showAlert(error.message);
    });
  }

  ngOnInit(): void {
    this.dataSource.getContacts();
  }

  ngAfterViewInit() {
    this.dataSource.counter$
      .pipe(
        tap((count) => {
          this.paginator.length = count;
        })
      )
      .subscribe();

    this.paginator.page
      .pipe(
        tap(() => this.getContacts())
      )
      .subscribe();
  }

  getContacts() {
    this.selection.clear();
    this.dataSource.getContacts(this.paginator.pageIndex, this.paginator.pageSize);
  }

  addContactFile() {
    const dialogRef = this.dialog.open(FileUploadComponent, {
      width: '250px',
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getContacts();
    });
  }

  sendEmail () {
    this.emailService.sendEmail(this.selection.selected, this.body).subscribe(result => {
      this.showAlert("Successful");
    }, error => {
      this.showAlert(error.message);
    });
  }

  showAlert(message: string) {
    const dialogRef = this.dialog.open(AlertModalComponent, {
      width: '200px',
      data: message
    });
    dialogRef.afterClosed().subscribe(result => {
      this.getContacts();
    });
  }
}

@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'dialog.html',
})
export class ContactModalComponent{

  constructor(
    public dialogRef: MatDialogRef<ContactModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    @Inject(ContactService) public contactService: ContactService,
    public dialog: MatDialog) {
    this.actionAdd = data.actionAdd;
    this.contact = data.contact ? data.contact : {pk:0, fullName:"", email:""};
    this.message = "";
  }

  contact : Contact;
  // @ts-ignore
  actionAdd : boolean;
  message : string;

  onNoClick(): void {
    this.dialogRef.close();
  }

  onUpdateClick(): void {
    this.contactService.updateContact(this.data.contact).subscribe(result => {
      this.showAlert("Successful");
    }, err => {
      this.message = err.error.email ? err.error.email : err.error;
      this.showAlert(this.message);
    });
  }

  onAddClick(): void {
    this.contactService.addContact(this.contact).subscribe(result => {
      this.showAlert("Successful");
    }, err => {
      this.message = err.error.email ? err.error.email : err.error;
      this.showAlert(this.message);
    });
  }

  showAlert(message: string) {
    const dialogRef = this.dialog.open(AlertModalComponent, {
      width: '200px',
      data: message
    });
    dialogRef.afterClosed().subscribe(result => {
      this.dialogRef.close();
    });
  }
}



@Component({
  selector: 'alert-dialog',
  templateUrl: 'alert.html',
})
export class AlertModalComponent{

  constructor(
    public dialogRef: MatDialogRef<AlertModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.message = data;
  }

  message : string;

  onClick() {
    this.dialogRef.close();
  }
}

