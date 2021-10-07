import { Component, OnInit } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileUploadService } from 'src/app/services/file-upload.service';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AlertModalComponent} from "../contact-table/contact-table.component";

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  selectedFiles?: FileList;
  currentFile?: File;
  progress = 0;
  message = '';

  fileInfos?: Observable<any>;

  constructor(public dialogRef: MatDialogRef<FileUploadComponent>, private uploadService: FileUploadService,
              public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }

  upload(): void {
    this.progress = 0;

    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);
      if (file) {
        this.currentFile = file;
        this.uploadService.upload(this.currentFile).subscribe(
          (event: any) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.progress = Math.round(100 * event.loaded / event.total);
            } else if (event instanceof HttpResponse) {
              this.message = event.body.message;
              this.showAlert("Successful");
            }
          },
          (err: any) => {
            this.progress = 0;

            if (err.error && err.error.message) {
              this.message = err.error.message;
            } else {
              this.message = err.error;
            }
            this.showAlert(this.message);
            this.currentFile = undefined;
          });
      }
      this.selectedFiles = undefined;
    }
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
